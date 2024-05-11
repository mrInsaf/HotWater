package com.example.mynfc.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

data class VodaUiState (
    var cardId: ByteArray? = byteArrayOf(),
    var sector10Key: ByteArray? = byteArrayOf(),
    var sector12Key: ByteArray? = byteArrayOf(),

    var balance: String = "",
    var serverBalance: String = "",
    var name: String = "",

    var toServerValue: String = "",
    var toCardValue: String = "",
    var newBalance: String = "",

    var isAddingBalance: Boolean = false,
    var isUpdatingServerBalance: Boolean = false,
    var isUpdatingCardBalance: Boolean = false,

    var completeWriting: Boolean = false,

)