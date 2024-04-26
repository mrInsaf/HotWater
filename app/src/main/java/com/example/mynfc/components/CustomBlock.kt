package com.example.mynfc.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CustomBlockColumn(title: String = "title", modifier: Modifier = Modifier, spacerHeight: Dp = paddingTop, content: @Composable () -> Unit, ) {
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
            .padding(horizontal = paddingStart, vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = TextStyle(fontSize = 12.sp, color = Color.LightGray),
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            )
        )
        content()
    }
    Spacer(modifier = modifier.size(spacerHeight))
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CustomBlockRow(title: String = "title", modifier: Modifier = Modifier, spacerHeight: Dp = paddingTop, content: @Composable () -> Unit, ) {
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
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CustomBlockRowPreview() {
    CustomBlockRow(title = "test", content = { Text(text = "text")})
}