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
import com.example.mynfc.AppLayout
import com.example.mynfc.R
import com.example.mynfc.components.CustomBlock
import com.example.mynfc.components.MainBlock
import com.example.mynfc.components.SecondaryBlock
import com.example.mynfc.components.TopUpBlockService
import com.example.mynfc.components.TopUpBlockUser
import com.example.mynfc.topUpHistory


@Composable
fun TopUpHistoryBlock(modifier: Modifier = Modifier) {
    CustomBlock(title = "История пополнений") {
        for (map in topUpHistory) {
            SecondaryBlock(mainInfo = map["value"], secondaryInfo = map["date"])
        }
    }
}


@Composable
fun TopUpHistoryGraphicBlock(modifier: Modifier = Modifier) {
    CustomBlock(title = "Статистика") {
    }
}

@Composable
fun CheckBalanceScreenUser(
    username: String,
    cardBalance: String,
    serverBalance: String,
    newBalance: String,
    completeWriting: Boolean,
    isAddingBalance: Boolean,
    service: Boolean,
    modifier: Modifier = Modifier,
    onNewBalanceChange: ((String) -> Unit) = {},
    onUpdateServerBalance: ((String) -> Unit),
    onDismiss: () -> Unit,
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
                MainBlock(
                    mainInfo = "¥   $cardBalance",
                    secondaryInfo = "Баланс на карте",
                    buttonText = "Перенести на сервер",
                    onButtonClick = {
//                        onUpdateServerBalance()
                    },
                    service = service,
                    iconId = R.drawable.cloud_computing,
                )
                MainBlock(
                    mainInfo = "¥   $serverBalance",
                    secondaryInfo = "Баланс на сервере",
                    buttonText = "Перенести на карту",
                    onButtonClick = {
                        println("yo")
                    },
                    service = service,
                    iconId = R.drawable.download,
                )
                TopUpBlockUser(
                    title = "Введите баланс",
                    onValueChange = {onNewBalanceChange(it)},
                    topUpValue = newBalance,
                )

                TopUpHistoryGraphicBlock()
            }
        }
    }


}

@Preview
@Composable
fun CheckBalanceScreenUserPreview() {
    CheckBalanceScreenUser(
        username = "IVANOV IVAN",
        cardBalance = "123",
        serverBalance = "0",
        isAddingBalance = false,
        completeWriting = false,
        service = false,
        onNewBalanceChange = {println(it)},
        onUpdateServerBalance = {},
        newBalance = "0"
    ) {

    }
}