package com.example.mynfc.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mynfc.R

@Composable
fun ServerBalanceTransactionDialogService(
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
            text = stringResource(R.string.write_to_server, newServerBalance),
            iconId = R.drawable.cloud_computing,
            onConfirmation = onToServerConfirmation,
            onDismissRequest = onToServerDismiss
        )
    }
    if(toCard) {
        MyDialog(
            text = stringResource(R.string.subtract_from_server, newCardBalance),
            iconId = R.drawable.download,
            onConfirmation = onToCardConfirmation,
            onDismissRequest = onToCardDismiss
        )
    }
}


@Preview
@Composable
fun ServerBalanceTransactionDialogPreview() {
    ServerBalanceTransactionDialogService(
        newServerBalance = "12",
        newCardBalance = "13",
        onToServerDismiss = {},
        onToServerConfirmation = {},
        onToCardDismiss = {},
        onToCardConfirmation = {},
        toServer = true,
        toCard = false
    )
}