package com.example.mynfc.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.mynfc.AppLayout
import com.example.mynfc.R
import com.example.mynfc.components.MainBlock
import com.example.mynfc.components.ServerBalanceTransactionDialog
import com.example.mynfc.components.TopUpBlockService

@Composable
fun CheckBalanceScreenService(
    username: String,
    cardBalance: String,
    serverBalance: String,
    newBalance: String,
    toCardValue: String,
    toServerValue: String,
    completeWriting: Boolean,
    isAddingBalance: Boolean,
    isUpdatingServerBalance: Boolean,
    isUpdatingCardBalance: Boolean,
    service: Boolean,
    modifier: Modifier = Modifier,
    onAddingBalanceChange: () -> Unit,
    onNewBalanceChange: ((String) -> Unit) = {},
    onToCardValueChange: ((String) -> Unit) = {},
    onToServerValueChange: ((String) -> Unit) = {},
    onUpdateServerBalance: () -> Unit,
    onUpdateServerBalanceBegin: () -> Unit,
    onDismissAddingBalance: () -> Unit,
    onDismissCompletedBalance: () -> Unit,
    onDismissUpdatingServerBalance: () -> Unit,
    ) {
    AppLayout {

        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                MainBlock(mainInfo = username, secondaryInfo = "Имя", service = service)
                TopUpBlockService(
                    title = "Баланс на карте",
                    topUpValue = toServerValue,
                    buttonText = "На сервер",
                    placeholderValue = cardBalance,
                    onClick = onUpdateServerBalanceBegin,
                    onValueChange = {onToServerValueChange(it)},
                    iconId = R.drawable.cloud_computing,
                )
                TopUpBlockService(
                    title = "Баланс на сервере",
                    topUpValue = toCardValue,
                    buttonText = "На карту",
                    placeholderValue = serverBalance,
                    onClick = onAddingBalanceChange,
                    onValueChange = {onToCardValueChange(it)},
                    iconId = R.drawable.download,
                )
                TopUpBlockService(
                    topUpValue = newBalance,
                    buttonText = "Записать",
                    placeholderValue = "",
                    onClick = onAddingBalanceChange,
                    onValueChange = {onNewBalanceChange(it)},
                    iconId = R.drawable.diskette
                )
                if (isAddingBalance) {
                    AlertDialog(
                        onDismissRequest = onDismissAddingBalance,
                        title = { Text(text = "Приложите карту") },
                        text = { Text("К записи $newBalance ¥") },
                        icon = {
                            Icon(painter = painterResource(id = R.drawable.contactless), contentDescription = "Example Icon")
                        },
                        confirmButton = {
                            Button(onDismissAddingBalance) {
                                Text("Oк", fontSize = 22.sp)
                            }
                        },
                        containerColor = Color.White
                    )
                }

                if (completeWriting) {
                    AlertDialog(
                        onDismissRequest = onDismissCompletedBalance,
                        title = { Text(text = "Баланс успешно записан") },
                        text = { Text(
                            text = if (!isUpdatingServerBalance) {
                                "Баланс карты -> $cardBalance ¥"
                            } else {
                                "Баланс карты -> $cardBalance ¥\n" +
                                "Баланс сервера -> $serverBalance ¥"
                            }
                        )},
                        icon = {
                            Icon(painter = painterResource(id = R.drawable.success), contentDescription = "Example Icon")
                        },
                        confirmButton = {
                            Button(onDismissCompletedBalance) {
                                Text("OK", fontSize = 22.sp)
                            }
                        }
                    )
                }

                if (isUpdatingServerBalance) {
                    ServerBalanceTransactionDialog(
                        newServerBalance = toServerValue,
                        toServer = true,
                        onDismissRequest = { onDismissUpdatingServerBalance() },
                        onConfirmation = { onUpdateServerBalance() })
                }

                TopUpHistoryGraphicBlock()
            }
        }
    }

}

@Preview
@Composable
fun CheckBalanceScreenServicePreview() {
    CheckBalanceScreenService(
        username = "IVANOV IVAN",
        cardBalance = "123",
        serverBalance = "0",
        isAddingBalance = false,
        isUpdatingServerBalance = false,
        isUpdatingCardBalance = false,
        completeWriting = false,
        service = true,
        onAddingBalanceChange = { ""},
        onNewBalanceChange = {println(it)},
        onUpdateServerBalance = {},
        newBalance = "0",
        toServerValue = "0",
        toCardValue = "0",
        onDismissAddingBalance = {},
        onDismissCompletedBalance = {},
        onUpdateServerBalanceBegin = {}
    ) {

    }
}