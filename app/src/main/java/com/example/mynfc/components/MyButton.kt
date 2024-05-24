package com.example.mynfc.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynfc.R

@Composable
fun MyButton(
    text: String,
    modifier: Modifier = Modifier,
    minWidth: Dp = 148.dp,
    onClick: () -> Unit,
    ) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff9CA8FF))
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.widthIn(min = minWidth)
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
fun ButtonWithIcon(
    enabled: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    minWidth: Dp = 128.dp,
    iconId: Int? = null,
    onClick: () -> Unit,
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff9CA8FF))
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.widthIn(min = minWidth)
        ) {
            if (iconId != null) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = "",
                    modifier = modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = text,
                fontSize = 12.sp,
            )
        }
    }
}

@Preview
@Composable
fun MyButtonPreview() {
    MyButton("Button text") {
        println("yo")
    }
}