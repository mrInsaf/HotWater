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
import com.example.mynfc.network.getCard
import com.example.mynfc.network.getCardKeys
import com.example.mynfc.network.getServerBalance
import com.example.mynfc.network.updateServerBalanceNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.math.BigInteger
import kotlin.coroutines.EmptyCoroutineContext

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
                                completeWriting = true,
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
        val cardId = readArray[0]

        var newBalance = ""
        var newServerBalance = ""

        for (i in 0..1) {
            val bIndex = mfc.sectorToBlock(12) + i
            try {
                mfc.connect()
                val auth = mfc.authenticateSectorWithKeyA(12, sector12Key)
                if (auth) {
                    if (uiState.value.isUpdatingServerBalance) {
                        val toServerValue = _uiState.value.toServerValue
                        val response: Deferred<String> = async {
                            updateServerBalanceNetwork(cardId = cardId, newBalance = toServerValue)
                        }
                        newServerBalance = response.await()
                        newBalance =
                            (uiState.value.balance.toFloat() - uiState.value.toServerValue.toFloat()).toString()
                        _uiState.update { currentState ->
                            currentState.copy(newBalance = newBalance)
                        }
                    }
                    else {
                        newBalance = uiState.value.newBalance
                    }
                    println("newbalance: ${uiState.value.newBalance}")
                    var floatBalance = uiState.value.newBalance.toFloat()
                    floatBalance *= 100
                    println("floatBalance: $floatBalance")

                    if (i == 1) {
                        floatBalance += 1
                    }

                    val resultBytes = calculateResultBytes(floatBalance)

                    try {
                        if (mfc != null && tag != null) {
                            if (i == 0) {
                                println(getHexString(resultBytes, resultBytes.size))
                                mfc.writeBlock(bIndex, resultBytes)
                                println("new balance: $newBalance")
                                if (!uiState.value.isUpdatingServerBalance) {
                                    _uiState.update { currrentState ->
                                        currrentState.copy(
                                            balance = newBalance,
                                            isAddingBalance = false,
                                        )
                                    }
                                }
                                else {
                                    _uiState.update { currrentState ->
                                        currrentState.copy(
                                            balance = newBalance,
                                            serverBalance = newServerBalance,
                                            isUpdatingServerBalance = false,
                                            isAddingBalance = false,
                                        )
                                    }
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

    private fun calculateResultBytes(floatBalance: Float): ByteArray {
        val intBalance = floatBalance.toInt()
        val bytesBalance = intToBytes(intBalance)
        var resultBytes = ByteArray(14)
        bytesBalance.copyInto(resultBytes, startIndex = 0)
        val crc = calculateCRC16Modbus(resultBytes)
        val bytesCrc = intToBytes(crc).sliceArray(2 until 4).reversedArray()
        println("crc len: ${bytesCrc.size}")
        resultBytes += bytesCrc
        return resultBytes
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

        var currentCardId: ByteArray? = uiState.value.cardId
        var currentSector10Key: ByteArray? = uiState.value.sector10Key
        var currentSector12Key: ByteArray? = uiState.value.sector12Key

        if (!currentCardId.contentEquals(cardId) || currentSector10Key?.size == 0 || currentSector12Key?.size!! == 0) {
            val response: Deferred<JSONObject> = async {
                debugMessage += "\nПолучаю ключи"
                getCard(cardId)
            }
            val cardInfo = response.await()
            val cardKeys = getCardKeys(cardInfo)
            val serverBalance = getServerBalance(cardInfo)

            currentSector10Key = cardKeys[0]
            currentSector12Key = cardKeys[1]

            _uiState.update { currentState ->
                currentState.copy(
                    sector10Key = cardKeys[0],
                    sector12Key = cardKeys[1],
                    cardId = cardId,
                    serverBalance = serverBalance
                )
            }
            debugMessage += "\nПолучил ключи " +
                    "${ getHexString(currentSector10Key, currentSector10Key!!.size) }, " +
                    getHexString(currentSector12Key, currentSector12Key!!.size)

            println("\nПолучил ключи " +
                    "${ getHexString(currentSector10Key, currentSector10Key!!.size) }, " +
                    getHexString(currentSector12Key, currentSector12Key!!.size))
        }
        else {
            currentSector10Key = uiState.value.sector10Key
            currentSector12Key = uiState.value.sector12Key
        }

        for (j in 0 until secCount) {
            val authKey = when(j) {
                10 -> currentSector10Key
                12 -> currentSector12Key
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
        return@coroutineScope arrayOf(cardId, currentSector10Key, currentSector12Key)
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

    fun onDismissAddingBalance() {
        _uiState.update { currentState ->
            currentState.copy(
                isAddingBalance = false,
            )
        }
    }

    fun onDismissCompletedBalance() {
        _uiState.update { currentState ->
            currentState.copy(
                completeWriting = false,
                newBalance = "",
                toServerValue = "",
            )
        }
    }

    fun onUpdateServerBalanceBegin() {
        _uiState.update { currentState ->
            currentState.copy(
                isUpdatingServerBalance = true,
            )
        }
    }

    fun onDismissUpdatingServerBalance() {
        _uiState.update { currentState ->
            currentState.copy(
                isUpdatingServerBalance = false,
            )
        }
    }

    fun onUpdateCardBalanceBegin() {
        _uiState.update { currentState ->
            currentState.copy(
                isUpdatingCardBalance = true,
            )
        }
    }

    suspend fun updateServerBalance() = coroutineScope {

        _uiState.update { currentState ->
            currentState.copy(
                isAddingBalance = true,
            )
        }
    }
}