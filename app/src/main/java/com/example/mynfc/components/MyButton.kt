package com.example.mynfc.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MyButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    ) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff9CA8FF))
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.widthIn(min = 148.dp)
        ) {
            Text(
                text = text,
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