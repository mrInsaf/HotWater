package com.example.mynfc.ui.voda

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat.recreate
import androidx.lifecycle.ViewModel
import com.example.mynfc.errorTypes.VodaErrorType
import com.example.mynfc.misc.calculateFloatBalance
import com.example.mynfc.misc.calculateResultBytes
import com.example.mynfc.misc.getHexString
import com.example.mynfc.misc.jsonStringToList
import com.example.mynfc.network.confirmTransaction
import com.example.mynfc.network.createTransactionService
import com.example.mynfc.network.getCard
import com.example.mynfc.network.getCardKeys
import com.example.mynfc.network.getServerBalance
import com.example.mynfc.network.getTransactionHistoryByCardId
import com.example.mynfc.network.updateServerBalanceNetworkService
import com.example.mynfc.network.updateServerBalanceNetworkUser
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
import java.net.ConnectException
import java.util.Locale
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
                    println("new card balance is: ${uiState.value.balance}")
                    val newServerBalance = confirmTransaction(
                        transactionId = uiState.value.transactionId,
                        confirm = true,
                        newCardBalance = uiState.value.balance
                        )
                    fetchTransactionHistory()
                    _uiState.update { currentState ->
                        currentState.copy(
                            completeWriting = true,
                            newBalance = "",
                            unacceptableUserInput = false,
                            isUpdatingBalance = false,
                            isUpdatingServerBalance = false,
                            isUpdatingCardBalance = false,
                            serverBalance = newServerBalance,
                        )
                    }
                }
                catch (e: Exception) {
                    _uiState.update {currentState ->
                        currentState.copy(
                            error = VodaErrorType.WRITING_ERROR,
                            isUpdatingBalance = false,
                            newBalance = "",
                            isUpdatingServerBalance = false,
                            isUpdatingCardBalance = false,
                            transactionId = "",
                        )
                    }
                }
            } else {
                try {
                    readData(tagFromIntent)
                } catch (e: ConnectException) {
                    println("Ошибка подключения: $e")
                    _uiState.update {
                        it.copy(
                            error = VodaErrorType.CONNECTION_ERROR,
                            isReading = false
                        )
                    }
                    debugMessage += "\n $e"
                } catch (e: Exception) {
                    println("Ошибка при чтении: $e")
                    _uiState.update {
                        it.copy(
                            error = VodaErrorType.READING_ERROR,
                            isReading = false
                        )
                    }
                    debugMessage += "\n $e"
                }

            }
            _uiState.update {
                it.copy(
                    isReading = false,
                    isWriting = false,
                )
            }
        }
    }


    private suspend fun writeBalance(tag: Tag?) = coroutineScope {
        println("started writing")
        _uiState.update { it.copy(isWriting = true) }
        val mfc = MifareClassic.get(tag)
        val readArray = readData(tag)

        val sector12Key = readArray[2]
        val cardId = readArray[0]
        println("cardId on write: $cardId")

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
                    val transactionValue = uiState.value.transactionValue
                    val toServerValue = uiState.value.toServerValue
                    println("uistate: " +
                            "${uiState.value.isUpdatingServerBalance}" +
                            "${uiState.value.isUpdatingCardBalance}" +
                            "${uiState.value.isUpdatingBalance}")

                    if (uiState.value.isUpdatingServerBalance || uiState.value.isUpdatingCardBalance) {
                        println("Just about to update server balance")
                        val serverResponse = updateServerBalanceNetworkUser(
                            cardId = cardId,
                            transactionValue = transactionValue,
                            newBalance = toServerValue,
                            )
                        val transactionId = serverResponse[0]
                        println("transactionId: $transactionId")
                        _uiState.update { it.copy(transactionId = transactionId) }
                    }
                    else if (uiState.value.isUpdatingBalance) {
                        println("yebani transaction value: $transactionValue")
                        val transactionId = createTransactionService(cardId = cardId, value = transactionValue)
                        _uiState.update { it.copy(transactionId = transactionId) }
                    }
                    println("new balance: $updatedBalance")
                    _uiState.update { it.copy(
                        balance = updatedBalance,
                        ) }
                }
                try {
                    mfc.writeBlock(bIndex, resultBytes)
                }
                catch (e: Exception) {
                    println("Yebani oshibka: $e")
                }
            } catch (e: Exception) {
                println("Error during writing balance: $e")
                debugMessage += "\n $e"
                if (uiState.value.transactionId != "") {
                    confirmTransaction(transactionId = uiState.value.transactionId, confirm = false)
                }
                throw e
            }
            finally {
                try {
                    mfc?.close()
                } catch (e: IOException) {
                    println("Error closing tag: $e")
                    if (uiState.value.transactionId != "") {
                        confirmTransaction(transactionId = uiState.value.transactionId, confirm = false)
                    }
                }
            }
        }
    }


    private suspend fun readData(tag: Tag?): Array<ByteArray?> = coroutineScope {
        val mfc = MifareClassic.get(tag)
        mfc.connect()
        _uiState.update { it.copy(isReading = true) }

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
            val cardData: Deferred<JSONObject> = async {
                debugMessage += "\nПолучаю ключи"
                getCard(cardId)
            }

            val cardInfo = cardData.await()
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
                )
            }

            fetchTransactionHistory()

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
        val newBalance = uiState.value.newBalance.toFloat()
        val currentBalance = uiState.value.balance.toFloat()
        val transactionValue = newBalance - currentBalance

        _uiState.update { currentState ->
            currentState.copy(
                isUpdatingBalance = true,
                transactionValue = transactionValue.toString()
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

        val transactionValue = -userInput
        val newServerBalance = serverBalance + userInput
        val newCardBalance = cardBalance - userInput

        println("transactionValue: $transactionValue")

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
                    newBalance = newCardBalance.toString(),
                    transactionValue = transactionValue.toString()
                )
            }

        }
    }
    fun onUpdateCardBalanceBeginUser() {
        val userInput = uiState.value.newBalance.toFloat()
        val cardBalance = uiState.value.balance.toFloat()
        val serverBalance = uiState.value.serverBalance.toFloat()

        val transactionValue = userInput
        val newServerBalance = serverBalance - userInput
        val newCardBalance = cardBalance + userInput
        println("transactionValue: $transactionValue")

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
                    newBalance = newCardBalance.toString(),
                    transactionValue = transactionValue.toString(),
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
            scope.launch {
                val response: Deferred<String> = async {
                    updateServerBalanceNetworkService(
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
        val currentBalance = uiState.value.balance.toFloat()
        val newBalance = currentBalance + toCardValue

        println("updateCardBalance toCardValue: $toCardValue, newbalance: $newBalance")
//        val transactionValue =
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
                error = null,
            )
        }
    }

    suspend fun fetchTransactionHistory() = coroutineScope {
        val cardId = uiState.value.cardId
        val transactionsData: Deferred<String> = async {
            debugMessage += "\nПолучаю историю транзакций"
            getTransactionHistoryByCardId(cardId)
        }

        val transactionsHistory = jsonStringToList(transactionsData.await()).reversed()
        println("transactionsInfo: $transactionsHistory")
        _uiState.update { currentState ->
            currentState.copy(
                transactionList = transactionsHistory
            )
        }
    }

    suspend fun fetchServerBalance() = coroutineScope {
        val cardId = uiState.value.cardId
        val cardData: Deferred<JSONObject> = async {
            debugMessage += "\nПолучаю ключи"
            getCard(cardId)
        }

        val cardInfo = cardData.await()
        val serverBalance = getServerBalance(cardInfo)

        _uiState.update { it.copy(serverBalance = serverBalance) }
        return@coroutineScope
    }

    fun changeLocale(context: Context, newLocale: String) {
        val locale = Locale(newLocale)
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        context.createConfigurationContext(configuration)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)

        _uiState.update { it.copy(currentLocale = newLocale) }
        val activity = context as? Activity
        activity?.recreate()
    }


}


