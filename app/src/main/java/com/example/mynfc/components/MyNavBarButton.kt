package com.example.mynfc.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynfc.R

@Composable
fun MyNavbarButton(
    iconId: Int,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Icon (
            painterResource(id = iconId),
            modifier = modifier.size(20.dp),
            contentDescription = text,
        )
        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(
                Font(R.font.montserrat_medium)
            ),
            fontSize = 12.sp,
            lineHeight = 12.sp,
            modifier = modifier.width(120.dp))
    }
}