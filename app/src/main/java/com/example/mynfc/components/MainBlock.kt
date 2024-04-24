package com.example.mynfc.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.mynfc.R

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainBlock(mainInfo: String, secondaryInfo: String, modifier: Modifier = Modifier) {
    CustomBlock(title = secondaryInfo) {
        Text(
            text = mainInfo,
            style = TextStyle(fontSize = 24.sp, color = Color.Black),
            fontFamily = FontFamily(
                Font(R.font.montserrat_medium)
            )
        )
    }
}