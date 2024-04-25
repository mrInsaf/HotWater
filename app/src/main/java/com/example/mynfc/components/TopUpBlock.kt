package com.example.mynfc.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynfc.R
import com.example.mynfc.ui.theme.paddingStart

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TopUpBlock(
    modifier: Modifier = Modifier,
    isAddingBalance: Boolean,
    onValueChange: (String) -> Unit,
    onAddingBalanceChange: () -> Unit) {
    var text by remember {
        mutableStateOf("")
    }
    Column {
        CustomBlock(title = "Введите новый баланс", spacerHeight = 0.dp, modifier = modifier) {
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    onValueChange(it) // Вызываем функцию обратного вызова при изменении значения
                },
                suffix = { Text ("¥", fontSize = 24.sp, fontFamily = FontFamily(Font(R.font.montserrat_medium))) },
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.montserrat_medium))
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(
                    disabledTextColor = Color.Transparent,
                    containerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = modifier.fillMaxWidth()
            )
        }
        Row(horizontalArrangement = Arrangement.End, modifier = modifier.fillMaxWidth().padding(horizontal = paddingStart)) {
            MyButton(text = "Записать", onClick = onAddingBalanceChange)
        }

    }

}

@Preview
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TopUpBlockPreview(modifier: Modifier = Modifier) {
    var topUpValue = "1"
    TopUpBlock(
        isAddingBalance = false,
        onValueChange = { value ->
            topUpValue = value // Сохраняем введенное значение в переменной topUpValue
        },
        onAddingBalanceChange = { println("yi")})
}