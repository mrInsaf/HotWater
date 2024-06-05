package com.example.mynfc.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
            text = stringResource(R.string.credit_to_server, newServerBalance),
            iconId = R.drawable.cloud_computing,
            onConfirmation = onToServerConfirmation,
            onDismissRequest = onToServerDismiss
        )
    }
    if(toCard) {
        MyDialog(
            text = stringResource(R.string.credit_to_card, newCardBalance),
            iconId = R.drawable.download,
            onConfirmation = onToCardConfirmation,
            onDismissRequest = onToCardDismiss
        )
    }
}


