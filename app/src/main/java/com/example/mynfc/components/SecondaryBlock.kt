package com.example.mynfc.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynfc.R
import com.example.mynfc.ui.theme.paddingStart
import com.example.mynfc.ui.theme.paddingTop

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SecondaryBlock(mainInfo: String?, secondaryInfo: String?, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .border(BorderStroke(1.dp, Color.LightGray), shape = RoundedCornerShape(16.dp))
            .padding(horizontal = paddingStart)

    ) {
        Text(
            text = secondaryInfo ?: "",
            style = TextStyle(fontSize = 12.sp, color = Color.LightGray),
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            )
        )
        Text(
            text = mainInfo ?: "",
            style = TextStyle(fontSize = 24.sp, color = Color(0xFF7E7E7E),
                fontFamily = FontFamily(
                    Font(R.font.montserrat_medium)
                )
            )
        )
    }
    Spacer(modifier = modifier.size(paddingTop))
}