package com.example.mynfc.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
                    text = stringResource(R.string.confirm),
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
                    text = stringResource(R.string.cancel),
                    fontSize = 14,
                    color = Color(0xff9CA8FF)
                )
            }
        },
        containerColor = Color.White
    )
}