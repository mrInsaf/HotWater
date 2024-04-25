package com.example.mynfc.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynfc.R
import com.example.mynfc.ui.theme.paddingStart
import com.example.mynfc.ui.theme.paddingTop

@Composable
fun Header(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(start = paddingStart, top = paddingTop, bottom = 38.dp)
    ) {
//        Icon(painter = painterResource(R.drawable.water), contentDescription = "water", Modifier.size(20.dp))
//        Spacer(modifier = modifier.size(8.dp))
        Text(
            text = stringResource(id = R.string.app_name),
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Black,
            ),
            fontFamily = FontFamily(
                Font(R.font.montserrat_bold)
            ),
        )
    }

}

@Preview
@Composable
fun HeaderPreview(modifier: Modifier = Modifier) {
    Header()
}
