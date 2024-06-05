package com.example.mynfc.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mynfc.AppLayout
import com.example.mynfc.R
import com.example.mynfc.components.MainBlock
import com.example.mynfc.components.ServerBalanceTransactionDialogUser
import com.example.mynfc.components.TopUpBlockUser
import com.example.mynfc.components.TransactionsHistoryGraphicBlock
import com.example.mynfc.components.SingleButtonDialog
import com.example.mynfc.errorTypes.VodaErrorType
import com.example.mynfc.ui.voda.VodaViewModel
import com.example.mynfc.ui.voda.listOfTransactions

@Composable
fun CheckBalanceScreenUser(
    vodaViewModel: VodaViewModel = viewModel(),
    username: String,
    cardBalance: String,
    serverBalance: String,
    newBalance: String,
    completeWriting: Boolean,
    isAddingBalance: Boolean,
    service: Boolean,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onNewBalanceChange: ((String) -> Unit) = {},
    transactionList: List<Map<String, Any>>,
    onUpdateServerBalance: ((String) -> Unit),
    onDismiss: () -> Unit,
    onDismissError: () -> Unit,
) {
    AppLayout(navController = navController) {
        val uiState = vodaViewModel.uiState.collectAsState()
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                MainBlock(mainInfo = username, secondaryInfo = stringResource(R.string.name), service = service)
                MainBlock(
                    mainInfo = "¥   $cardBalance",
                    secondaryInfo = stringResource(R.string.card_balance),
                    gifEnabled = uiState.value.isReading,
                    onButtonClick = {
//                        onUpdateServerBalance()
                    },
                    service = false,
                    iconId = R.drawable.cloud_computing,
                )
                MainBlock(
                    mainInfo = "¥   $serverBalance",
                    secondaryInfo = stringResource(R.string.server_balance),
                    gifEnabled = uiState.value.isReading,
                    onButtonClick = {
                        println("yo")
                    },
                    service = false,
                    iconId = R.drawable.download,
                )
                TopUpBlockUser(
                    title = stringResource(R.string.enter_balance),
                    onValueChange = {onNewBalanceChange(it)},
                    topUpValue = newBalance,
                    enabled = uiState.value.userInputEnabled,
                    unacceptableInput = uiState.value.unacceptableUserInput,
                    onUpdatingServerBalanceChange = { vodaViewModel.onUpdateServerBalanceBeginUser() },
                    onUpdatingCardBalanceChange = {vodaViewModel.onUpdateCardBalanceBeginUser() }
                )

                if (isAddingBalance) {
                    SingleButtonDialog(
                        title = stringResource(R.string.attach_card),
                        onDismissRequest = { vodaViewModel.onDismissAddingBalance() },
                        text = stringResource(R.string.to_write_balance, uiState.value.transactionValue),
                        iconId = R.drawable.contactless,
                        buttonText = stringResource(R.string.cancel),
                        gifEnabled = uiState.value.isWriting
                    )
                }

                if (completeWriting) {
                    SingleButtonDialog(
                        title = stringResource(R.string.balance_successfully_written),
                        text = stringResource(R.string.current_balances, cardBalance, serverBalance),
                        onDismissRequest = { vodaViewModel.onDismissCompletedBalance() },
                        iconId = R.drawable.success,
                    )
                }
                if (uiState.value.isUpdatingServerBalance || uiState.value.isUpdatingCardBalance) {
                    ServerBalanceTransactionDialogUser(
                        newServerBalance = ((uiState.value.transactionValue).toFloat() * (-1f)).toString(),
                        newCardBalance = uiState.value.transactionValue,
                        toServer = uiState.value.isUpdatingServerBalance,
                        toCard = uiState.value.isUpdatingCardBalance,
                        onToCardDismiss = { vodaViewModel.onDismissUpdatingCardBalance()},
                        onToCardConfirmation = { vodaViewModel.updateCardBalanceUser()},
                        onToServerDismiss = { vodaViewModel.onDismissUpdatingServerBalance()},
                        onToServerConfirmation = { vodaViewModel.updateServerBalanceUser()},
                    )
                }

                when (uiState.value.error) {
                    VodaErrorType.WRITING_ERROR -> SingleButtonDialog(
                        title = stringResource(R.string.writing_error),
                        onDismissRequest = { onDismissError() })
                    VodaErrorType.READING_ERROR -> SingleButtonDialog(
                        title = stringResource(R.string.reading_error),
                        onDismissRequest = { onDismissError() })
                    VodaErrorType.CONNECTION_ERROR -> SingleButtonDialog(
                        title = stringResource(R.string.connection_error),
                        onDismissRequest = { onDismissError() })
                    else -> Unit
                }


                if (transactionList.isNotEmpty()) {
                    TransactionsHistoryGraphicBlock(transactionList)
                }

            }
        }
    }
}


@Preview
@Composable
fun CheckBalanceScreenUserPreview() {
    CheckBalanceScreenUser(
        username = "IVANOV IVAN",
        cardBalance = "123",
        serverBalance = "0",
        isAddingBalance = false,
        completeWriting = false,
        service = false,
        onNewBalanceChange = {println(it)},
        onUpdateServerBalance = {},
        newBalance = "0",
        transactionList = listOfTransactions,
        navController = rememberNavController(),
        onDismiss = {},
    ) {

    }
}