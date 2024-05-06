package com.example.mynfc.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynfc.R
import com.example.mynfc.ui.theme.paddingStart
import com.example.mynfc.ui.theme.paddingTop

@Composable
fun CustomBlock(title: String = "title", modifier: Modifier = Modifier, spacerHeight: Dp = paddingTop, content: @Composable () -> Unit, ) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .advancedShadow(
                color = Color.Black,
                alpha = 0.05f,
                cornersRadius = 16.dp,
                shadowBlurRadius = 20.dp,
                offsetY = 8.dp,
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(vertical = 12.dp, horizontal = paddingStart)
    ) {
        Text(
            text = title,
            style = TextStyle(fontSize = 12.sp, color = Color.LightGray),
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.fillMaxWidth()
        ) {
            content()
        }
    }
    Spacer(modifier = modifier.size(spacerHeight))
}

@Preview
@Composable
fun CustomBlockRowPreview() {
    CustomBlock(title = "test", content = { Text(text = "text")})
}
