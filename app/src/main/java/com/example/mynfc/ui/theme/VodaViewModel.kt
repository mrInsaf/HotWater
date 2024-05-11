package com.example.mynfc.ui.theme

import android.content.ContentValues
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mynfc.calculateCRC16Modbus
import com.example.mynfc.misc.getHexString
import com.example.mynfc.network.getCardKeys
import com.example.mynfc.network.updateServerBalanceNetwork
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException
import java.math.BigInteger

class VodaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(VodaUiState())
    val uiState: StateFlow<VodaUiState> = _uiState.asStateFlow()

    var debugMessage by mutableStateOf("Начальное сообщение")

    suspend fun resolveIntent(intent: Intent) = coroutineScope {
        val action = intent.action
        if (NfcAdapter.ACTION_TECH_DISCOVERED == action) {
            val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (_uiState.value.isAddingBalance) {
                    writeBalance(tagFromIntent)
                    _uiState.update { currentState ->
                            currentState.copy(
                                completeWriting = true
                            )
                }
            }
            else {
                try {
                    readData(tagFromIntent)
                }
                catch (e: Exception) {
                    println(e)
                    debugMessage += "\n $e"
                }
            }
        }
    }


    private suspend fun writeBalance(tag: Tag?) = coroutineScope {
        println("started writing")
        val mfc = MifareClassic.get(tag)
        val readArray = readData(tag)

        val sector12Key = readArray[2]

        for (i in 0..1) {
            val bIndex = mfc.sectorToBlock(12) + i
            try {
                mfc.connect()
                val auth = mfc.authenticateSectorWithKeyA(12, sector12Key)
                if (auth) {
                    var floatBalance = _uiState.value.newBalance.toFloat()
                    floatBalance *= 100
                    if (i == 1) {
                        floatBalance += 1
                    }
                    val intBalance = floatBalance.toInt()
                    val bytesBalance = intToBytes(intBalance)
                    var resultBytes = ByteArray(14)
                    bytesBalance.copyInto(resultBytes, startIndex = 0)
                    val crc = calculateCRC16Modbus(resultBytes)
                    val bytesCrc = intToBytes(crc).sliceArray(2 until 4).reversedArray()
                    println("crc len: ${bytesCrc.size}")
                    resultBytes += bytesCrc

                    try {
                        if (mfc != null && tag != null) {
                            println(getHexString(resultBytes, resultBytes.size))
                            mfc.writeBlock(bIndex, resultBytes)
                            if (i == 0) {
                                _uiState.update { currentState ->
                                    currentState.copy(
                                        balance = (floatBalance / 100).toString()
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        println("Error writing block: $e")
                        debugMessage += "\n $e"
                    }
                } else {
                    println("No auth for writing")
                }
            } catch (e: Exception) {
                println("Error connecting to tag: $e")
                debugMessage += "\n $e"
            } finally {
                try {
                    mfc?.close()
                } catch (e: IOException) {
                    println("Error closing tag: $e")
                    debugMessage += "\n $e"
                }
            }
        }
    }

    private suspend fun readData(tag: Tag?): Array<ByteArray?> = coroutineScope {
        val mfc = MifareClassic.get(tag)
        mfc.connect()
        val cardId = tag?.id
        var data: ByteArray?
        var authA: Boolean
        val secCount = mfc.sectorCount
        var bCount: Int
        var bIndex: Int

        println("card id: ${ getHexString(cardId, cardId?.size ?: 0) }")
        println("sector count: $secCount")

        _uiState.update { currentState ->
            currentState.copy(
                cardId = cardId
            )
        }

        val response: Deferred<Array<ByteArray>> = async {
            debugMessage += "\nПолучаю ключи"
            getCardKeys(cardId)
        }
        val cardKeys = response.await()

        val sector10Key = cardKeys[0]
        val sector12Key = cardKeys[1]

        debugMessage += "\nПолучил ключи " +
                "${ getHexString(sector10Key, sector10Key.size) }, " +
                getHexString(sector12Key, sector12Key.size)

        for (j in 0 until secCount) {
            val authKey = when(j) {
                10 -> sector10Key
                12 -> sector12Key
                else -> MifareClassic.KEY_DEFAULT
            }
            authA = mfc.authenticateSectorWithKeyA(j, authKey)
            if (authA) {
                bCount = mfc.getBlockCountInSector(j)
                bIndex = mfc.sectorToBlock(j)
                for (i in 0 until bCount) {
                    data = mfc.readBlock(bIndex)
                    Log.i(ContentValues.TAG, getHexString(data, data?.size ?: 0))
//                        println(getHexString(data, data?.size ?: 0))
                    if (j == 12 && i == 0) {
                        val balanceData = data.take(4).toByteArray()
                        val bigInteger = BigInteger(balanceData)
                        _uiState.update { currentState ->
                            currentState.copy(
                                balance = (bigInteger.toFloat() / 100).toString()
                            )
                        }
                        println(getHexString(data, data.size))
                    }
                    else if (j == 10 && i == 2) {
                        print("block $i ")
                        println(getHexString(data, data?.size ?: 0))

                        _uiState.update { currentState ->
                            currentState.copy(
                                name = String(data.copyOfRange(0, data.size - 2))
                            )
                        }
                    }
                    bIndex++
                }
            } else {
                println("sector $j: Auth failed")
            }
        }
        mfc.close()
        return@coroutineScope arrayOf(cardId, sector10Key, sector12Key)
    }


    fun intToBytes(value: Int): ByteArray {
        val result = ByteArray(4)
        result[0] = (value shr 24).toByte()
        result[1] = (value shr 16).toByte()
        result[2] = (value shr 8).toByte()
        result[3] = value.toByte()
        return result
    }

    fun onAddingBalanceChange() {
        _uiState.update { currentState ->
            currentState.copy(
                isAddingBalance = true
            )
        }
        println("is adding balance ${_uiState.value.isAddingBalance}")
    }

    fun onNewBalanceChange(newBalance: String) {
        _uiState.update { currentState ->
            currentState.copy(
                newBalance = newBalance
            )
        }
    }

    fun onToCardValueChange(newCardValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                toCardValue = newCardValue
            )
        }
    }

    fun onToServerValueChange(newServerValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                toServerValue = newServerValue
            )
        }
    }

    fun onDismiss() {
        _uiState.update { currentState ->
            currentState.copy(
                isAddingBalance = false,
                completeWriting = false
            )
        }
    }

    suspend fun updateServerBalance(newBalance: String) {
        val cardId = _uiState.value.cardId
        updateServerBalanceNetwork(cardId = cardId, newBalance = newBalance)
    }
}