package com.example.mynfc.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mynfc.R

@Composable
fun ServerBalanceTransactionDialog(
    newServerBalance: String,
    toServer: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(id = R.drawable.cloud_computing), contentDescription = "Example Icon")
        },
        title = {
            MyText(
                text = "Записать $newServerBalance ¥ на сервер?",
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

@Preview
@Composable
fun ServerBalanceTransactionDialogPreview() {
    ServerBalanceTransactionDialog(
        newServerBalance = "12",
        onConfirmation = {},
        onDismissRequest = {},
        toServer = true,
    )
}