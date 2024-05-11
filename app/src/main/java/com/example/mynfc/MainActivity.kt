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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mynfc.components.Header
import com.example.mynfc.components.MyNavbar
import com.example.mynfc.screens.CheckBalanceScreenService

import com.example.mynfc.screens.CheckBalanceScreenUser
import com.example.mynfc.screens.StartScreen

import com.example.mynfc.ui.theme.MyNFCTheme
import com.example.mynfc.ui.theme.VodaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

const val service: Boolean = false

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
        data object CheckBalanceService : Screen("check_balance_service")
        data object CheckBalanceUser : Screen("check_balance_user")
    }

    @Composable
    fun App(vodaViewModel: VodaViewModel = viewModel()) {
        val navController = rememberNavController()

        val uiState = vodaViewModel.uiState.collectAsState()
        val checkBalanceScreen =  if(service) Screen.CheckBalanceService.route else Screen.CheckBalanceUser.route

        NavHost(navController = navController, startDestination = checkBalanceScreen) {
            composable(Screen.Start.route) {
                StartScreen()
            }
            composable(Screen.CheckBalanceUser.route) {
                CheckBalanceScreenUser(
                    username = uiState.value.name,
                    cardBalance = uiState.value.balance,
                    serverBalance = uiState.value.serverBalance,
                    completeWriting = uiState.value.completeWriting,
                    isAddingBalance = uiState.value.isAddingBalance,
                    newBalance = uiState.value.newBalance,
                    onNewBalanceChange = { vodaViewModel.onNewBalanceChange(it) },
                    service = service,
                    onDismiss = { vodaViewModel.onDismiss() },
                    onUpdateServerBalance = {},
                )
            }
            composable(Screen.CheckBalanceService.route) {
                CheckBalanceScreenService(
                    username = uiState.value.name,
                    cardBalance = uiState.value.balance,
                    serverBalance = uiState.value.serverBalance,
                    completeWriting = uiState.value.completeWriting,
                    isAddingBalance = uiState.value.isAddingBalance,
                    newBalance = uiState.value.newBalance,
                    toCardValue = uiState.value.toCardValue,
                    toServerValue = uiState.value.toServerValue,
                    onAddingBalanceChange = { vodaViewModel.onAddingBalanceChange() },
                    onNewBalanceChange = { vodaViewModel.onNewBalanceChange(it) },
                    onToCardValueChange = { vodaViewModel.onToCardValueChange(it) },
                    onToServerValueChange = { vodaViewModel.onToServerValueChange(it) },
                    service = service,
                    onDismiss = { vodaViewModel.onDismiss() },
                    onUpdateServerBalance = {},
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
    fun AppLayout(content: @Composable () -> Unit) {
        Scaffold(
            topBar = { Header() },
            bottomBar = { MyNavbar() },
            content = {
                // Применение внутреннего отступа для содержимого
                Surface(Modifier.padding(it), color = Color.White) {
                    content()
                }
            },
            containerColor = Color.White
        )
    }
