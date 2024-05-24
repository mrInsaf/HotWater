package com.example.mynfc.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mynfc.R

@Composable
fun MyDialog(
    text: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    iconId: Int,
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(id = iconId), contentDescription = "Example Icon")
        },
        title = {
            MyText(
                text = text,
                fontSize = 20,
                fontFamily = R.font.montserrat_regular
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                MyText(
                    text = "Подтвердить",
                    fontSize = 14,
                    color = Color(0xff9CA8FF)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                MyText(
                    text = "Отмена",
                    fontSize = 14,
                    color = Color(0xff9CA8FF)
                )
            }
        },
        containerColor = Color.White
    )
}

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
            text = "Записать $newServerBalance ¥ на сервер?",
            iconId = R.drawable.cloud_computing,
            onConfirmation = onToServerConfirmation,
            onDismissRequest = onToServerDismiss
        )
    }
    if(toCard) {
        MyDialog(
            text = "Списать $newCardBalance ¥ с сервера?",
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