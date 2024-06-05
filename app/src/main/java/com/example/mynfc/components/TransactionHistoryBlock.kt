package com.example.mynfc.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mynfc.R
import com.example.mynfc.misc.createPointsList
import com.example.mynfc.misc.retrieveTransactionData

@Composable
fun TransactionHistoryBlock(
    transactionHistory: List<Map<String, Any>>,
    modifier: Modifier = Modifier
) {
    val transactionsData = retrieveTransactionData(transactionHistory)

    CustomBlock(title = stringResource(R.string.top_up_history)) {
        LazyColumn(modifier = modifier) {
            items(transactionsData) { map ->
                SecondaryBlock(
                    mainInfo = map["value"].toString() ,
                    secondaryInfo = "${map["day"]}.${map["month"]}.${map["year"]} ${map["time"]}"
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