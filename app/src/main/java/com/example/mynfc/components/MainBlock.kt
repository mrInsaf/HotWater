package com.example.mynfc.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.mynfc.R


@Composable
fun MainBlock(
    mainInfo: String,
    secondaryInfo: String,
    modifier: Modifier = Modifier,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    iconId: Int? = null,
    service: Boolean
)  {
    CustomBlock(
        title = secondaryInfo,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mainInfo,
                style = TextStyle(fontSize = 24.sp, color = Color.Black),
                fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                modifier = modifier
//                    .weight(4f)
            )
            if (service && buttonText != null && onButtonClick != null) {
                ButtonWithIcon(
                    text = buttonText,
                    onClick = onButtonClick,
                    iconId = iconId
                )
            }
        }
    }
}

@Preview
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainBlockPreview() {
    MainBlock("yoo", "yoo", buttonText = "yooщщщщ", onButtonClick = {
        println("yo")
    }, service = true)
}