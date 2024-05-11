package com.example.mynfc.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.sp
import com.example.mynfc.R

@Composable
fun MyText(
    text: String,
    fontSize: Int = 24,
    fontFamily: Int = R.font.montserrat_medium,
    color: Color = Color.Black
) {
    Text(
        text = text,
        style = TextStyle(fontSize = fontSize.sp, color = color),
        fontFamily = FontFamily(Font(fontFamily)),
    )
}