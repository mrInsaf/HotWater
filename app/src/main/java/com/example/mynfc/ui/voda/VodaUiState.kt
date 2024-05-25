package com.example.mynfc.ui.voda

data class VodaUiState (
    var service: Boolean = false,

    var cardId: ByteArray? = byteArrayOf(),
    var sector10Key: ByteArray? = byteArrayOf(),
    var sector12Key: ByteArray? = byteArrayOf(),

    var balance: String = "",
    var serverBalance: String = "",
    var name: String = "",

    var cardBalanceUpdateAvailable: Boolean = false,
    var serverBalanceUpdateAvailable: Boolean = false,

    var unacceptableToCardValue: Boolean = false,
    var unacceptableToServerValue: Boolean = false,
    var unacceptableUserInput: Boolean = false,

    var toServerValue: String = "",
    var toCardValue: String = "",
    var newBalance: String = "",

    var isUpdatingBalance: Boolean = false,
    var isUpdatingServerBalance: Boolean = false,
    var isUpdatingCardBalance: Boolean = false,

    var completeWriting: Boolean = false,

    var userInputEnabled: Boolean = false,

    var transactionValue: String = "",
    var transactionId: String = "",

    var transactionList: List<Map<String, Any>> = listOf(mapOf()),

    var errorOnWriting: Boolean = false,
)

