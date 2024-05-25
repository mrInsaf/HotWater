package com.example.mynfc.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mynfc.MainActivity
import com.example.mynfc.R
import com.example.mynfc.ui.theme.paddingStart


@Composable
fun MyNavbar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .advancedShadow(
                color = Color.Black,
                alpha = 0.05f,
                cornersRadius = 16.dp,
                shadowBlurRadius = 20.dp,
                offsetY = (-8).dp,
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
            .heightIn(min = 10.dp, max = 80.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingStart, vertical = 16.dp)
        ) {
            MyNavbarButton(text = "Проверить баланс", iconId = R.drawable.statistics) {
                navController.navigate(MainActivity.Screen.CheckBalance.route)
            }
            MyNavbarButton(text = "Посмотреть историю", iconId = R.drawable.history) {
                navController.navigate(MainActivity.Screen.TransactionHistory.route)
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun MyNavbarPreview() {
    MyNavbar(rememberNavController())
}