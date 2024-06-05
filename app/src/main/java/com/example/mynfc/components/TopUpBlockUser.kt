package com.example.mynfc.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynfc.R

@Composable
fun TopUpBlockUser(
    title: String,
    onValueChange: (String) -> Unit,
    topUpValue: String,
    unacceptableInput: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onUpdatingServerBalanceChange: () -> Unit,
    onUpdatingCardBalanceChange: () -> Unit,
) {
    CustomBlock(title = title, modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text (stringResource(R.string.currency_symbol), fontSize = 24.sp, fontFamily = FontFamily(Font(R.font.montserrat_medium)))
                MyTextField(
                    enabled = enabled,
                    onValueChange = onValueChange,
                    topUpValue = topUpValue,
                    placeholderValue = "",
                    unacceptableInput = unacceptableInput,
                )
            }

            Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Button(
                    onClick = onUpdatingCardBalanceChange,
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
                        Text(text = stringResource(R.string.to_card))
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                Button(
                    onClick = onUpdatingServerBalanceChange,
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
                        Text(text = stringResource(R.string.to_server))
                    }
                }

            }
        }
    }
}


@Preview
@Composable
fun TopUpBlockUserPreview() {
    TopUpBlockUser(
        title = "yo",
        onValueChange = {},
        topUpValue = "123",
        enabled = true,
        unacceptableInput = false,
        onUpdatingServerBalanceChange = {},
        onUpdatingCardBalanceChange = {}
    )
}