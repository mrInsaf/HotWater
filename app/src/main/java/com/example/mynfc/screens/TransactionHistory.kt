package com.example.mynfc.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mynfc.AppLayout
import com.example.mynfc.components.TransactionHistoryBlock
import com.example.mynfc.ui.voda.VodaUiState

@Composable
fun TransactionHistoryScreen(
    uiState: State<VodaUiState>?,
    navController: NavHostController
) {
    AppLayout(navController = navController) {
        if (uiState != null) {
            TransactionHistoryBlock(uiState.value.transactionList)
        }
    }
}



@Preview
@Composable
fun TransactionHistoryScreenPreview() {
    TransactionHistoryScreen(
        uiState = null,
        navController = rememberNavController()
    )
}