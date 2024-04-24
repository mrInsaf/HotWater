package com.example.mynfc.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynfc.R
import com.example.mynfc.components.CustomBlock
import com.example.mynfc.components.MainBlock
import com.example.mynfc.components.MyNavbar
import com.example.mynfc.components.SecondaryBlock
import com.example.mynfc.topUpHistory
import com.example.mynfc.ui.theme.paddingStart
import com.example.mynfc.ui.theme.paddingTop

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TopUpHistoryBlock(modifier: Modifier = Modifier) {
    CustomBlock(title = "История пополнений") {
        for (map in topUpHistory) {
            SecondaryBlock(mainInfo = map["value"], secondaryInfo = map["date"])
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TopUpHistoryGraphicBlock(modifier: Modifier = Modifier) {
    CustomBlock(title = "Статистика") {
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun CheckBalanceScreenPreview() {
    CheckBalanceScreen(username = "Инсаф", cardBalance = "123", serverBalance = "0")
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CheckBalanceScreen(
    username: String,
    cardBalance: String,
    serverBalance: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Text(
            text = "My Voda",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Black,
            ),
            fontFamily = FontFamily(
                Font(R.font.montserrat_bold)
            ),
            modifier = modifier
                .padding(start = paddingStart, top = paddingTop, bottom = 38.dp)
        )
        MainBlock(mainInfo = username, secondaryInfo = "Имя")
        MainBlock(
            mainInfo = "$cardBalance ¥",
            secondaryInfo = "Баланс на карте",
            buttonText = "Перенести на сервер",
        ) {
            println("yo")
        }
        MainBlock(
            mainInfo = "$serverBalance ¥",
            secondaryInfo = "Баланс на сервере",
            buttonText = "Перенести на карту",
        ) {
            println("yo")
        }
//        TopUpHistoryBlock()
        TopUpHistoryGraphicBlock()

        Spacer(modifier = Modifier.weight(1f))
        MyNavbar()
    }
}