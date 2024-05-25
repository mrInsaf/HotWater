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
import com.example.mynfc.components.CustomBlock
import com.example.mynfc.components.MainBlock
import com.example.mynfc.components.MyText
import com.example.mynfc.components.SecondaryBlock
import com.example.mynfc.components.ServerBalanceTransactionDialogUser
import com.example.mynfc.components.TopUpBlockUser
import com.example.mynfc.topUpHistory
import com.example.mynfc.ui.voda.VodaViewModel


@Composable
fun TopUpHistoryGraphicBlock(modifier: Modifier = Modifier) {
    CustomBlock(title = "Статистика") {
    }
}

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
    onUpdateServerBalance: ((String) -> Unit),
    onDismiss: () -> Unit,
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
                MainBlock(
                    mainInfo = "¥   $cardBalance",
                    secondaryInfo = "Баланс на карте",
                    buttonText = "Перенести на сервер",
                    onButtonClick = {
//                        onUpdateServerBalance()
                    },
                    service = service,
                    iconId = R.drawable.cloud_computing,
                )
                MainBlock(
                    mainInfo = "¥   $serverBalance",
                    secondaryInfo = "Баланс на сервере",
                    buttonText = "Перенести на карту",
                    onButtonClick = {
                        println("yo")
                    },
                    service = service,
                    iconId = R.drawable.download,
                )
                TopUpBlockUser(
                    title = "Введите баланс",
                    onValueChange = {onNewBalanceChange(it)},
                    topUpValue = newBalance,
                    enabled = uiState.value.userInputEnabled,
                    unacceptableInput = uiState.value.unacceptableUserInput,
                    onUpdatingServerBalanceChange = { vodaViewModel.onUpdateServerBalanceBeginUser() },
                    onUpdatingCardBalanceChange = {vodaViewModel.onUpdateCardBalanceBeginUser() }
                )

                if (isAddingBalance) {
                    AlertDialog(
                        onDismissRequest = { vodaViewModel.onDismissAddingBalance() },
                        title = { Text(text = "Приложите карту") },
                        text = { Text("К записи $newBalance ¥") },
                        icon = {
                            Icon(painter = painterResource(id = R.drawable.contactless), contentDescription = "Example Icon")
                        },
                        confirmButton = {
                            Button({ vodaViewModel.onDismissAddingBalance() }) {
                                Text("Отмена", fontSize = 22.sp)
                            }
                        },
                        containerColor = Color.White
                    )
                }

                if (completeWriting) {
                    AlertDialog(
                        onDismissRequest = { vodaViewModel.onDismissCompletedBalance() },
                        title = { Text(text = "Баланс успешно записан") },
                        text = { Text(
                                "Баланс карты -> $cardBalance ¥\n" +
                                        "Баланс сервера -> $serverBalance ¥"
                        )
                        },
                        icon = {
                            Icon(painter = painterResource(id = R.drawable.success), contentDescription = "Example Icon")
                        },
                        confirmButton = {
                            Button({ vodaViewModel.onDismissCompletedBalance() }) {
                                Text("OK", fontSize = 22.sp)
                            }
                        }
                    )
                }

                ServerBalanceTransactionDialogUser(
                    newServerBalance = uiState.value.toServerValue,
                    newCardBalance = uiState.value.toCardValue,
                    toServer = uiState.value.isUpdatingServerBalance,
                    toCard = uiState.value.isUpdatingCardBalance,
                    onToCardDismiss = { vodaViewModel.onDismissUpdatingCardBalance()},
                    onToCardConfirmation = { vodaViewModel.updateCardBalanceUser()},
                    onToServerDismiss = { vodaViewModel.onDismissUpdatingServerBalance()},
                    onToServerConfirmation = { vodaViewModel.updateServerBalanceUser()},
                )

                if (uiState.value.errorOnWriting) {
                    AlertDialog(
                        onDismissRequest = { vodaViewModel.onDismissError() },
                        title = { Text(text = "Ошибка при записи баланса") },
                        text = { Text(
                            "Попробуйте еще раз"
                            )
                        },
                        icon = {
                            Icon(painter = painterResource(id = R.drawable.warning), contentDescription = "Example Icon")
                        },
                        confirmButton = {
                            Button({ vodaViewModel.onDismissCompletedBalance() }) {
                                Text("OK", fontSize = 22.sp)
                            }
                        }
                    )
                }

                TopUpHistoryGraphicBlock()
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
        navController = rememberNavController(),
    ) {

    }
}