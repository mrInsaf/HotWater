package com.example.mynfc.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mynfc.AppLayout
import com.example.mynfc.R
import com.example.mynfc.components.CustomBlock

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    navhost: NavHostController,
) {
    AppLayout(navhost) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxHeight()
        ) {
            CustomBlock(
                title = "",
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(440.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.contactless),
                        modifier = modifier
                            .size(218.dp),
                        contentDescription = "приложите карту",
                        tint = Color.LightGray
                    )
                    Spacer(modifier = modifier.size(52.dp))
                    Text(
                        text = "Приложите карту",
                        style = TextStyle(fontSize = 18.sp, color = Color.LightGray),
                        fontFamily = FontFamily(
                            Font(R.font.montserrat_regular)
                        ),
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun StartScreenPreview() {
    StartScreen(navhost = rememberNavController())
}