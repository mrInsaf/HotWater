package com.example.mynfc.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.mynfc.R
import com.example.mynfc.components.CustomBlockColumn
import com.example.mynfc.components.Header
import com.example.mynfc.components.MainBlock
import com.example.mynfc.components.MyNavbar
import com.example.mynfc.components.SecondaryBlock
import com.example.mynfc.components.TopUpBlock
import com.example.mynfc.topUpHistory

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TopUpHistoryBlock(modifier: Modifier = Modifier) {
    CustomBlockColumn(title = "История пополнений") {
        for (map in topUpHistory) {
            SecondaryBlock(mainInfo = map["value"], secondaryInfo = map["date"])
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TopUpHistoryGraphicBlock(modifier: Modifier = Modifier) {
    CustomBlockColumn(title = "Статистика") {
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun CheckBalanceScreenPreview() {
    CheckBalanceScreen(
        username = "Инсаф",
        cardBalance = "123",
        serverBalance = "0",
        isAddingBalance = false,
        completeWriting = false,
        onAddingBalanceChange = { ""}) {

    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CheckBalanceScreen(
    username: String,
    cardBalance: String,
    serverBalance: String,
    completeWriting: Boolean,
    isAddingBalance: Boolean,
    modifier: Modifier = Modifier,
    onAddingBalanceChange: () -> Unit,
    onNewBalanceChange: ((String) -> Unit) = {},
    onDismiss: () -> Unit,
) {
    var topUpValue by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Header()
            MainBlock(mainInfo = username, secondaryInfo = "Имя")
            MainBlock(
                mainInfo = "¥ $cardBalance",
                secondaryInfo = "Баланс на карте",
                buttonText = "Перенести на сервер",
            ) {
                println("yo")
            }
            MainBlock(
                mainInfo = "¥ $serverBalance",
                secondaryInfo = "Баланс на сервере",
                buttonText = "Перенести на карту",
            ) {
                println("yo")
            }
//        TopUpHistoryBlock()
            TopUpBlock(
                isAddingBalance = isAddingBalance,
                onValueChange = {onNewBalanceChange(it); topUpValue = it},
                onAddingBalanceChange = onAddingBalanceChange


            )
            if (isAddingBalance) {
                AlertDialog(
                    onDismissRequest = onDismiss,
                    title = { Text(text = "Приложите карту") },
                    text = { Text("К записи $topUpValue ¥") },
                    icon = {
                        Icon(painter = painterResource(id = R.drawable.contactless), contentDescription = "Example Icon")
                    },
                    confirmButton = {
                        Button(onDismiss) {
                            Text("OK", fontSize = 22.sp)
                        }
                    }
                )
            }

            if (completeWriting) {
                AlertDialog(
                    onDismissRequest = onDismiss,
                    title = { Text(text = "Баланс успешно записан") },
                    text = { Text("На баланс записано $topUpValue ¥") },
                    icon = {
                        Icon(painter = painterResource(id = R.drawable.success), contentDescription = "Example Icon")
                    },
                    confirmButton = {
                        Button(onDismiss) {
                            Text("OK", fontSize = 22.sp)
                        }
                    }
                )
            }

            TopUpHistoryGraphicBlock()
        }
        MyNavbar()
    }

}