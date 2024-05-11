package com.example.mynfc.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ServerBalanceTransactionAlert(
    maxBalance: Int,
    toServer: Boolean,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = { },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "This is a minimal dialog",
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
//                TopUpBlock(
//                    title = "Введите баланс (максимум $maxBalance)",
//                    onValueChange = {},
//                    onAddingBalanceChange = { /*TODO*/ },
//                    service = true
//                )
            }

        }
    }
}

@Preview
@Composable
fun ServerBalanceTransactionAlertPreview() {
    ServerBalanceTransactionAlert(maxBalance = 100, toServer = true)
}