package com.example.mynfc

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentFilter.MalformedMimeTypeException
import android.nfc.NfcAdapter
import android.nfc.tech.MifareClassic
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mynfc.components.Header
import com.example.mynfc.components.MyNavbar
import com.example.mynfc.screens.CheckBalanceScreenService

import com.example.mynfc.screens.CheckBalanceScreenUser
import com.example.mynfc.screens.StartScreen
import com.example.mynfc.screens.TransactionHistoryScreen

import com.example.mynfc.ui.theme.MyNFCTheme
import com.example.mynfc.ui.voda.VodaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext


val topUpHistory = listOf(
    mapOf("date" to "20.04.2024", "value" to "+50 ¥"),
    mapOf("date" to "21.04.2024", "value" to "-36 ¥"),
)

class MainActivity : ComponentActivity() {
    private lateinit var mAdapter: NfcAdapter
    private lateinit var mPendingIntent: PendingIntent
    private lateinit var mFilters: Array<IntentFilter>
    private lateinit var mTechLists: Array<Array<String>>

    val vodaViewModel = VodaViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking() {
            launch {
            }
        }
        mAdapter = NfcAdapter.getDefaultAdapter(this)
        mPendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )
        val ndef = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)

        try {
            ndef.addDataType("*/*")
        } catch (e: MalformedMimeTypeException) {
            throw RuntimeException("fail", e)
        }
        mFilters = arrayOf(ndef)
        mTechLists = arrayOf(
            arrayOf(
                MifareClassic::class.java.name
            )
        )

        setContent {
            MyNFCTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(vodaViewModel = vodaViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val scope = CoroutineScope(EmptyCoroutineContext)
        val job = scope.launch {
            vodaViewModel.resolveIntent(intent!!)
        }
    }


    sealed class Screen(val route: String) {
        data object Start : Screen("start")
        data object CheckBalance : Screen("check_balance")
        data object TransactionHistory : Screen("transaction_history")
    }

    @Composable
    fun App(vodaViewModel: VodaViewModel = viewModel()) {
        val navController = rememberNavController()

        val uiState = vodaViewModel.uiState.collectAsState()

        NavHost(navController = navController, startDestination = Screen.CheckBalance.route) {
            composable(Screen.Start.route) {
                StartScreen(navhost = navController)
            }
            if (uiState.value.service) {
                composable(Screen.CheckBalance.route) {
                    CheckBalanceScreenService(
                        vodaViewModel = vodaViewModel,
                        username = uiState.value.name,
                        cardBalance = uiState.value.balance,
                        serverBalance = uiState.value.serverBalance,
                        completeWriting = uiState.value.completeWriting,
                        isAddingBalance = uiState.value.isUpdatingBalance,
                        newBalance = uiState.value.newBalance,
                        toCardValue = uiState.value.toCardValue,
                        toServerValue = uiState.value.toServerValue,
                        onAddingBalanceChange = { vodaViewModel.onUpdatingBalanceChange() },
                        onNewBalanceChange = { vodaViewModel.onNewBalanceChange(it) },
                        onToServerValueChange = { vodaViewModel.onToServerValueChange(it) },
                        service = uiState.value.service,
                        onDismissAddingBalance = { vodaViewModel.onDismissAddingBalance() },
                        onDismissCompletedBalance = { vodaViewModel.onDismissCompletedBalance() },
                        onUpdateServerBalance = {
                            val scope = CoroutineScope(EmptyCoroutineContext)
                            scope.launch {
                                vodaViewModel.updateServerBalance()
                            }
                        },
                        isUpdatingServerBalance = uiState.value.isUpdatingServerBalance,
                        isUpdatingCardBalance = uiState.value.isUpdatingCardBalance,
                        onDismissUpdatingServerBalance = {vodaViewModel.onDismissUpdatingServerBalance()},
                        onUpdateServerBalanceBegin = { vodaViewModel.onUpdateServerBalanceBegin() },
                        onDismissUpdatingCardBalance = { vodaViewModel.onDismissUpdatingCardBalance() },
                        onUpdateCardBalance = { vodaViewModel.updateCardBalance() },
                        navController = navController
                    )
                }
            }
            else {
                composable(Screen.CheckBalance.route) {
                    CheckBalanceScreenUser(
                        vodaViewModel = vodaViewModel,
                        username = uiState.value.name,
                        cardBalance = uiState.value.balance,
                        serverBalance = uiState.value.serverBalance,
                        completeWriting = uiState.value.completeWriting,
                        isAddingBalance = uiState.value.isUpdatingBalance,
                        newBalance = uiState.value.newBalance,
                        onNewBalanceChange = { vodaViewModel.onNewBalanceChange(it) },
                        service = uiState.value.service,
                        onDismiss = { vodaViewModel.onDismissAddingBalance() },
                        onUpdateServerBalance = {},
                        navController = navController,
                    )
                }
            }
            composable(Screen.TransactionHistory.route) {
                TransactionHistoryScreen(
                    uiState = uiState,
                    navController = navController
                )
            }
        }
    }

    @Preview
    @Composable
    fun PreviewApp() {
        App()
    }
}
    @Composable
    fun AppLayout(navController: NavHostController, content: @Composable () -> Unit) {
        Scaffold(
            topBar = { Header() },
            bottomBar = { MyNavbar(navController) },
            content = {
                // Применение внутреннего отступа для содержимого
                Surface(Modifier.padding(it), color = Color.White) {
                    content()
                }
            },
            containerColor = Color.White
        )
    }
