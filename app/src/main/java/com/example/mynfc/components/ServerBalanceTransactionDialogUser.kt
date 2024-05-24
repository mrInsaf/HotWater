package com.example.mynfc.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mynfc.R

@Composable
fun ServerBalanceTransactionDialogUser(
    newServerBalance: String,
    newCardBalance: String,
    toServer: Boolean,
    toCard: Boolean,
    onToServerDismiss: () -> Unit,
    onToServerConfirmation: () -> Unit,
    onToCardDismiss: () -> Unit,
    onToCardConfirmation: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (toServer) {
        MyDialog(
            text = "Записать $newServerBalance ¥ на сервер?",
            iconId = R.drawable.cloud_computing,
            onConfirmation = onToServerConfirmation,
            onDismissRequest = onToServerDismiss
        )
    }
    if(toCard) {
        MyDialog(
            text = "Записать $newCardBalance ¥ на карту?",
            iconId = R.drawable.download,
            onConfirmation = onToCardConfirmation,
            onDismissRequest = onToCardDismiss
        )
    }
}

