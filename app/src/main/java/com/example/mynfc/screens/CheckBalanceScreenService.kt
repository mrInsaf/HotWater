package com.example.mynfc.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mynfc.AppLayout
import com.example.mynfc.R
import com.example.mynfc.components.MainBlock
import com.example.mynfc.components.ServerBalanceTransactionDialogService
import com.example.mynfc.components.TopUpBlockService
import com.example.mynfc.ui.voda.VodaViewModel

@Composable
fun CheckBalanceScreenService(
    vodaViewModel: VodaViewModel = viewModel(),

    username: String,

    cardBalance: String,
    serverBalance: String,

    newBalance: String,

    toCardValue: String,
    toServerValue: String,

    completeWriting: Boolean,
    isAddingBalance: Boolean,

    isUpdatingServerBalance: Boolean,
    isUpdatingCardBalance: Boolean,

    service: Boolean,

    navController: NavHostController,

    modifier: Modifier = Modifier,

    onNewBalanceChange: ((String) -> Unit) = {},
    onToServerValueChange: ((String) -> Unit) = {},

    onAddingBalanceChange: () -> Unit,
    onUpdateServerBalanceBegin: () -> Unit,
    onUpdateServerBalance: () -> Unit,
    onUpdateCardBalance: () -> Unit,


    onDismissAddingBalance: () -> Unit,
    onDismissCompletedBalance: () -> Unit,
    onDismissUpdatingServerBalance: () -> Unit,
    onDismissUpdatingCardBalance: () -> Unit,
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
                MainBlock(mainInfo = username, secondaryInfo = "Имя", service = service)
                TopUpBlockService(
                    enabled = true,
                    unacceptableInput = false,
                    title = "Баланс на карте",
                    topUpValue = newBalance,
                    buttonText = "Записать",
                    placeholderValue = cardBalance,
                    onClick = onAddingBalanceChange,
                    onValueChange = {onNewBalanceChange(it)},
                    iconId = R.drawable.diskette,
                )
                TopUpBlockService(
                    enabled = true,
                    unacceptableInput = false,
                    title = "Баланс на сервере",
                    topUpValue = toServerValue,
                    buttonText = "Записать",
                    placeholderValue = serverBalance,
                    onClick = onUpdateServerBalanceBegin,
                    onValueChange = {onToServerValueChange(it)},
                    iconId = R.drawable.cloud_computing,
                )
                if (isAddingBalance) {
                    AlertDialog(
                        onDismissRequest = onDismissAddingBalance,
                        title = { Text(text = "Приложите карту") },
                        text = { Text("К записи $newBalance ¥") },
                        icon = {
                            Icon(painter = painterResource(id = R.drawable.contactless), contentDescription = "Example Icon")
                        },
                        confirmButton = {
                            Button(onDismissAddingBalance) {
                                Text("Отмена", fontSize = 22.sp)
                            }
                        },
                        containerColor = Color.White
                    )
                }

                if (completeWriting) {
                    AlertDialog(
                        onDismissRequest = onDismissCompletedBalance,
                        title = { Text(text = "Баланс успешно записан") },
                        text = { Text(
                            text = if (!isUpdatingServerBalance || !isUpdatingCardBalance) {
                                "Баланс карты -> $cardBalance ¥"
                            } else {
                                "Баланс карты -> $cardBalance ¥\n" +
                                "Баланс сервера -> $serverBalance ¥"
                            }
                        )},
                        icon = {
                            Icon(painter = painterResource(id = R.drawable.success), contentDescription = "Example Icon")
                        },
                        confirmButton = {
                            Button(onDismissCompletedBalance) {
                                Text("OK", fontSize = 22.sp)
                            }
                        }
                    )
                }

                ServerBalanceTransactionDialogService(
                    newServerBalance = toServerValue,
                    newCardBalance = toCardValue,
                    toServer = isUpdatingServerBalance,
                    toCard = isUpdatingCardBalance,
                    onToCardDismiss = { onDismissUpdatingCardBalance() },
                    onToCardConfirmation = { onUpdateCardBalance() },
                    onToServerDismiss = { onDismissUpdatingServerBalance() },
                    onToServerConfirmation = { onUpdateServerBalance() },
                )

//                TopUpHistoryGraphicBlock()
            }
        }
    }

}

@Preview
@Composable
fun CheckBalanceScreenServicePreview() {
    CheckBalanceScreenService(
        username = "IVANOV IVAN",
        cardBalance = "123",
        serverBalance = "0",
        isAddingBalance = false,
        isUpdatingServerBalance = false,
        isUpdatingCardBalance = false,
        completeWriting = false,
        service = true,
        onAddingBalanceChange = { },
        onNewBalanceChange = {println(it)},
        onUpdateServerBalance = {},
        newBalance = "0",
        toServerValue = "0",
        toCardValue = "0",
        onDismissAddingBalance = {},
        onDismissCompletedBalance = {},
        onUpdateServerBalanceBegin = {},
        onDismissUpdatingServerBalance = {},
        onUpdateCardBalance = {},
        navController = rememberNavController(),
    ) {

    }
}