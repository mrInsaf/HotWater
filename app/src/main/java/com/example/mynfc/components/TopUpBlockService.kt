package com.example.mynfc.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.mynfc.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextField(
    enabled: Boolean,
    unacceptableInput: Boolean,
    onValueChange: (String) -> Unit,
    topUpValue: String,
    placeholderValue: String,
    modifier: Modifier = Modifier
) {

//    TextField(
//        value = topUpValue,
//        onValueChange = { onValueChange(it) },
//            textStyle = TextStyle(
//            fontSize = 24.sp,
//            color = Color.Black,
//            fontFamily = FontFamily(Font(R.font.montserrat_medium))
//        ),
//        modifier = modifier.width(100.dp),
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
//    )

    TextField(
        enabled = enabled,
        isError = unacceptableInput,
        value = topUpValue,
        onValueChange = { onValueChange(it) },
//        prefix = { Text ("¥", fontSize = 24.sp, fontFamily = FontFamily(Font(R.font.montserrat_medium))) },
        textStyle = TextStyle(
            fontSize = 24.sp,
            color = Color.Black,
            fontFamily = FontFamily(Font(R.font.montserrat_medium))
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            errorContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Red,
            errorLeadingIconColor = Color.Red,
        ),
        placeholder = { Text(placeholderValue, fontSize = 24.sp, fontFamily = FontFamily(Font(R.font.montserrat_medium))) },
        modifier = modifier.fillMaxWidth(fraction = 0.5f)
    )
}

@Composable
fun TopUpBlockService(
    enabled: Boolean,
    unacceptableInput: Boolean,
    title: String = "Введите баланс",
    buttonText: String,
    topUpValue: String,
    placeholderValue: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    iconId: Int? = null,
) {

        CustomBlock(title = title, modifier = modifier) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth(),

            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¥", fontSize = 24.sp, fontFamily = FontFamily(Font(R.font.montserrat_medium)))
                    MyTextField(
                        enabled = enabled,
                        unacceptableInput = unacceptableInput,
                        onValueChange = onValueChange,
                        topUpValue = topUpValue,
                        placeholderValue = placeholderValue,
                        )
                }
                ButtonWithIcon(
                    enabled = enabled,
                    text = buttonText,
                    onClick = onClick,
                    iconId = iconId,
                )
            }
        }
    }

@Preview
@Composable
fun TopUpBlockPreview(modifier: Modifier = Modifier) {
    var topUpValue = "1"
    TopUpBlockService(
        onValueChange = { value ->
            topUpValue = value // Сохраняем введенное значение в переменной topUpValue
        },
        onClick = { println("yi")},
        buttonText = "Записать",
        topUpValue = topUpValue,
        placeholderValue = "",
        enabled = true,
        unacceptableInput = true,
    )
}