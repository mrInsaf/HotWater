package com.example.mynfc.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mynfc.R

@Composable
fun SingleButtonDialog(
    title: String,
    text: String = "",
    buttonText: String = "Ок",
    onDismissRequest: () -> Unit,
    iconId: Int = R.drawable.warning,
    gifEnabled: Boolean = false,
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(id = iconId), contentDescription = "Example Icon")
        },
        title = {
            MyText(
                text = title,
                fontSize = 20,
                fontFamily = R.font.montserrat_medium
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                MyText(
                    text = text,
                    fontSize = 16,
                    fontFamily = R.font.montserrat_regular
                )
                if (gifEnabled) {
                    GifImage(
                        gifId = R.drawable.loading,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                MyText(
                    text = buttonText,
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
fun MyErrorDialogPreview() {
    SingleButtonDialog(
        onDismissRequest = {},
        title = "Оошибка",
        iconId = R.drawable.warning,
        text = "текст"
    )
}