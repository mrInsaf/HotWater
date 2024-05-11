package com.example.mynfc.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

data class VodaUiState (
    var cardId: ByteArray? = byteArrayOf(),
    var balance: String = "",
    var serverBalance: String = "",
    var name: String = "",
    var isAddingBalance: Boolean = false,
    var newBalance: String = "",
    var completeWriting: Boolean = false,

    var toServerValue: String = "",
    var toCardValue: String = "",
)