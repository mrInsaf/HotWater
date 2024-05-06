package com.example.mynfc.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynfc.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextField(
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember {
        mutableStateOf("")
    }
    TextField(
        value = text,
        onValueChange = {
            text = it
            onValueChange(it)
        },
        prefix = { Text ("¥", fontSize = 24.sp, fontFamily = FontFamily(Font(R.font.montserrat_medium))) },
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
            disabledIndicatorColor = Color.Transparent,
        ),
        modifier = modifier.fillMaxWidth(fraction = 0.3f)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopUpBlock(
    modifier: Modifier = Modifier,
    isAddingBalance: Boolean,
    onValueChange: (String) -> Unit,
    onAddingBalanceChange: () -> Unit,
    iconId: Int? = null,
    service: Boolean
) {

        CustomBlock(title = "Введите баланс", modifier = modifier) {
            if (service) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.fillMaxWidth(),

                ) {
                    MyTextField(onValueChange = onValueChange)
                    ButtonWithIcon(
                        text = "Записать",
                        onClick = onAddingBalanceChange,
                        iconId = iconId,
                    )
                }
            }
            else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MyTextField(onValueChange = onValueChange)
                    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        Button(
                            onClick = { /*TODO*/ },
                            modifier = modifier
                                .weight(1f)
                            ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.download),
                                    contentDescription = "",
                                    modifier = modifier.size(20.dp))
                                Text(text = "На карту")
                            }
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Button(
                            onClick = { /*TODO*/ },
                            modifier = modifier
                                .weight(1f)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.cloud_computing),
                                    contentDescription = "",
                                    modifier = modifier.size(20.dp))
                                Text(text = "На сервер")
                            }
                        }

                    }
                }
            }

        }
    }

@Preview
@Composable
fun TopUpBlockPreview(modifier: Modifier = Modifier) {
    var topUpValue = "1"
    TopUpBlock(
        isAddingBalance = false,
        onValueChange = { value ->
            topUpValue = value // Сохраняем введенное значение в переменной topUpValue
        },
        onAddingBalanceChange = { println("yi")},
        service = false
    )
}