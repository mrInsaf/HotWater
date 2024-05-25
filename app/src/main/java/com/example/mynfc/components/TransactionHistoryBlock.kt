package com.example.mynfc.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mynfc.topUpHistory

@Composable
fun TransactionHistoryBlock(
    transactionHistory: List<Map<String, Any>>,
    modifier: Modifier = Modifier
) {
    CustomBlock(title = "История пополнений") {
        LazyColumn(modifier = modifier) {
            items(transactionHistory) { map ->
                SecondaryBlock(
                    mainInfo = map["value"].toString() ,
                    secondaryInfo = map["date"].toString()
                )
            }
        }
    }
}

@Preview
@Composable
fun TransactionHistoryBlockPreview() {
    TransactionHistoryBlock(listOf(mapOf()))
}