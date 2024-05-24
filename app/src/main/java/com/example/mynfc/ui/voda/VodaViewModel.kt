package com.example.mynfc.ui.voda

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
import com.example.mynfc.misc.calculateFloatBalance
import com.example.mynfc.misc.calculateResultBytes
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
import okhttp3.internal.closeQuietly
import org.json.JSONObject
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
            if (_uiState.value.isUpdatingBalance) {
                try {
                    writeBalance(tagFromIntent)
                    _uiState.update { currentState ->
                        currentState.copy(
                            completeWriting = true,
                            newBalance = "",
                            unacceptableUserInput = false,
                        )
                    }
                }
                catch (e: Exception) {
                    _uiState.update {currentState ->
                        currentState.copy(

                        )
                    }
                }

            } else {
                try {
                    readData(tagFromIntent)
                } catch (e: Exception) {
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

        for (i in 0..1) {
            val bIndex = mfc.sectorToBlock(12) + i
            try {
                mfc?.connect()
                if (!mfc?.authenticateSectorWithKeyA(12, sector12Key)!!) {
                    println("No auth for writing")
                    return@coroutineScope
                }

                var floatBalance = calculateFloatBalance(uiState)
                if (i == 1) {
                    floatBalance += 1
                }
                val resultBytes = calculateResultBytes(floatBalance)

                println(getHexString(resultBytes, resultBytes.size))

                if (i == 0) {
                    val updatedBalance = (floatBalance / 100).toString()
                    if (uiState.value.isUpdatingServerBalance || uiState.value.isUpdatingCardBalance) {
                        println("Just about to update server balance")
                        val updatedServerBalance = updateServerBalanceNetwork(
                            cardId = cardId,
                            newBalance = uiState.value.toServerValue
                        )
                        println("updatedServerBalance: $updatedServerBalance, uiState.value.serverBalance: ${uiState.value.serverBalance}")
                        if (updatedServerBalance == uiState.value.serverBalance) {
                            println("Баланс на сервере не изменился")
                            throw Exception("Баланс на сервере не изменился")
                        }
                        mfc.writeBlock(bIndex, resultBytes)
                        _uiState.update { it.copy(serverBalance = updatedServerBalance) }
                    }
                    println("new balance: $updatedBalance")
                    _uiState.update { it.copy(balance = updatedBalance, isUpdatingBalance = false) }
                }
            } catch (e: Exception) {
                println("Error during writing balance: $e")
                debugMessage += "\n $e"
            } finally {
                mfc?.closeQuietly()
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

        println("card id: ${getHexString(cardId, cardId?.size ?: 0)}")
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
                    serverBalance = serverBalance,
//                    userInputEnabled = false,
                )
            }
            debugMessage += "\nПолучил ключи " +
                    "${getHexString(currentSector10Key, currentSector10Key.size)}, " +
                    getHexString(currentSector12Key, currentSector12Key.size)

            println(
                "\nПолучил ключи " +
                        "${getHexString(currentSector10Key, currentSector10Key.size)}, " +
                        getHexString(currentSector12Key, currentSector12Key.size)
            )
        } else {
            currentSector10Key = uiState.value.sector10Key
            currentSector12Key = uiState.value.sector12Key
        }

        for (j in 0 until secCount) {
            val authKey = when (j) {
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
                                balance = (bigInteger.toFloat() / 100).toString(),
                                userInputEnabled = true,
                            )
                        }
                        println(getHexString(data, data.size))
                    } else if (j == 10 && i == 2) {
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

    fun onUpdatingBalanceChange() {
        _uiState.update { currentState ->
            currentState.copy(
                isUpdatingBalance = true
            )
        }
        println("is adding balance ${_uiState.value.isUpdatingBalance}")
    }

    fun onNewBalanceChange(newBalance: String) {
        _uiState.update { currentState ->
            currentState.copy(
                newBalance = newBalance
            )
        }
    }


    fun onToServerValueChange(newServerValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                toServerValue = newServerValue
            )
        }
        if (uiState.value.toServerValue == "") {
            uiState.value.serverBalanceUpdateAvailable = false
        } else {
            val toServerValue = uiState.value.toServerValue.toFloat()
            val currentBalance = uiState.value.balance.toFloat()

            if (toServerValue > currentBalance) {
                _uiState.update { currentState ->
                    currentState.copy(serverBalanceUpdateAvailable = false)
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(serverBalanceUpdateAvailable = true)
                }
            }

            if (toServerValue > currentBalance) {
                _uiState.update { currentState ->
                    currentState.copy(unacceptableToServerValue = true)
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(unacceptableToServerValue = false)
                }
            }
        }


    }

    fun onDismissAddingBalance() {
        _uiState.update { currentState ->
            currentState.copy(
                isUpdatingBalance = false,
            )
        }
    }

    fun onDismissCompletedBalance() {
        _uiState.update { currentState ->
            currentState.copy(
                completeWriting = false,
                newBalance = "",
                toServerValue = "",
                toCardValue = "",
                isUpdatingServerBalance = false,
                isUpdatingCardBalance = false,
                cardBalanceUpdateAvailable = false,
                serverBalanceUpdateAvailable = false,
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

    fun onUpdateServerBalanceBeginUser() {
        val userInput = uiState.value.newBalance.toFloat()
        val serverBalance = uiState.value.serverBalance.toFloat()
        val cardBalance = uiState.value.balance.toFloat()
        val newServerBalance = serverBalance + userInput
        val newCardBalance = cardBalance - userInput

        if (userInput > cardBalance) {
            _uiState.update { currentState ->
                currentState.copy(
                    unacceptableUserInput = true,
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isUpdatingServerBalance = true,
                    toCardValue = newCardBalance.toString(),
                    toServerValue = newServerBalance.toString(),
                    newBalance = newCardBalance.toString()
                )
            }

        }
    }
    fun onUpdateCardBalanceBeginUser() {
        val userInput = uiState.value.newBalance.toFloat()
        val cardBalance = uiState.value.balance.toFloat()
        val serverBalance = uiState.value.serverBalance.toFloat()
        val newServerBalance = serverBalance - userInput
        val newCardBalance = cardBalance + userInput

        if (userInput > serverBalance) {
            _uiState.update { currentState ->
                currentState.copy(
                    unacceptableUserInput = true,
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isUpdatingCardBalance = true,
                    toCardValue = newCardBalance.toString(),
                    toServerValue = newServerBalance.toString(),
                    newBalance = newCardBalance.toString()
                )
            }

        }
    }

    fun updateServerBalanceUser() {
        _uiState.update { currentState ->
            currentState.copy(
                isUpdatingBalance = true,
            )
        }
    }

    fun updateCardBalanceUser() {
        _uiState.update { currentState ->
            currentState.copy(
                isUpdatingBalance = true,
            )
        }
    }

    fun onDismissUpdatingServerBalance() {
        _uiState.update { currentState ->
            currentState.copy(
                isUpdatingServerBalance = false,
                newBalance = "",
            )
        }
    }

    fun updateServerBalance() {
        val toServerValue = uiState.value.toServerValue.toFloat()
        val newBalance = uiState.value.balance.toFloat() - toServerValue

        if (!uiState.value.service) {
            _uiState.update { currentState ->
                currentState.copy(
                    isUpdatingBalance = true,
                    newBalance = newBalance.toString()
                )
            }
        } else {
            val scope = CoroutineScope(EmptyCoroutineContext)
            val job = scope.launch {
                val response: Deferred<String> = async {
                    updateServerBalanceNetwork(
                        cardId = uiState.value.cardId,
                        newBalance = uiState.value.toServerValue
                    )
                }
                val newServerBalance = response.await()
                _uiState.update { currentState ->
                    currentState.copy(
                        isUpdatingServerBalance = false,
                        serverBalance = newServerBalance,
                        toServerValue = ""
                    )
                }
            }
        }
    }

    fun onDismissUpdatingCardBalance() {
        _uiState.update { currentState ->
            currentState.copy(
                isUpdatingCardBalance = false,
                newBalance = "",
            )
        }
    }

    fun updateCardBalance() {
        val toCardValue = uiState.value.toCardValue.toFloat()
        val newBalance = uiState.value.balance.toFloat() + toCardValue
        _uiState.update { currentState ->
            currentState.copy(
                isUpdatingBalance = true,
                newBalance = newBalance.toString()
            )
        }
    }

    fun onDismissError() {
        _uiState.update { currentState ->
            currentState.copy(
                errorOnWriting = false
            )
        }
    }

}