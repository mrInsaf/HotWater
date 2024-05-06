package com.example.mynfc

import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentFilter.MalformedMimeTypeException
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mynfc.components.Header
import com.example.mynfc.components.MyNavbar
import com.example.mynfc.misc.getHexString
import com.example.mynfc.misc.hexStringToByteArray
import com.example.mynfc.network.getCard
import com.example.mynfc.network.getCardKeys

import com.example.mynfc.screens.CheckBalanceScreen
import com.example.mynfc.screens.StartScreen

import com.example.mynfc.ui.theme.MyNFCTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.math.BigInteger
import kotlin.coroutines.EmptyCoroutineContext

const val service: Boolean = true

var balance by mutableStateOf("")
var name by mutableStateOf("")
var isAddingBalance by  mutableStateOf(false)
var newBalance by mutableStateOf("0")
var debugMessage by mutableStateOf("Начальное сообщение")
var completeWriting by mutableStateOf(false)

val topUpHistory = listOf(
    mapOf("date" to "20.04.2024", "value" to "+50 ¥"),
    mapOf("date" to "21.04.2024", "value" to "-36 ¥"),
)

class MainActivity : ComponentActivity() {
    private lateinit var mAdapter: NfcAdapter
    private lateinit var mPendingIntent: PendingIntent
    private lateinit var mFilters: Array<IntentFilter>
    private lateinit var mTechLists: Array<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking() {
            launch {
            }
        }
        mAdapter = NfcAdapter.getDefaultAdapter(this);
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
                    App()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val scope = CoroutineScope(EmptyCoroutineContext)
        val job = scope.launch {
            resolveIntent(intent!!, isAddingBalance)
            withContext(Dispatchers.Main) {
                isAddingBalance = false
                println(isAddingBalance)
            }
        }
    }

    private suspend fun resolveIntent(intent: Intent, writeBalance: Boolean) = coroutineScope {
        val action = intent.action
        if (NfcAdapter.ACTION_TECH_DISCOVERED == action) {
            val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (writeBalance) {
                try {
                    writeBalance(tagFromIntent)
                    completeWriting = true
                }
                catch (e: Exception) {
                    println(e)
                    debugMessage += "\n $e"
                }
            }
            else {
                try {
                    readData(tagFromIntent)
                }
                catch (e: Exception) {
                    println(e)
                    debugMessage += "\n $e"
                }
            }
        }

    }

    private suspend fun writeBalance(tag: Tag?) = coroutineScope {
        println("started writing")
        val mfc = MifareClassic.get(tag)
        val readArray = readData(tag)

        val cardId = readArray[0]
        val sector10Key = readArray[1]
        val sector12Key = readArray[2]

        for (i in 0..1) {
            val bIndex = mfc.sectorToBlock(12) + i
            try {
                mfc.connect()
                val auth = mfc.authenticateSectorWithKeyA(12, sector12Key)
                if (auth) {
                    var floatBalance = newBalance.toFloat()
                    floatBalance *= 100
                    if (i == 1) {
                        floatBalance += 1
                    }
                    val intBalance = floatBalance.toInt()
                    val bytesBalance = intToBytes(intBalance)
                    var resultBytes = ByteArray(14)
                    bytesBalance.copyInto(resultBytes, startIndex = 0)
                    val crc = calculateCRC16Modbus(resultBytes)
                    val bytesCrc = intToBytes(crc).sliceArray(2 until 4).reversedArray()
                    println("crc len: ${bytesCrc.size}")
                    resultBytes += bytesCrc

                    try {
                        if (mfc != null && tag != null) {
                            println(getHexString(resultBytes, resultBytes.size))
                            mfc.writeBlock(bIndex, resultBytes)
                            if (i == 0) {
                                balance = (intBalance / 100).toString()
                            }
                        }
                    } catch (e: Exception) {
                        println("Error writing block: $e")
                        debugMessage += "\n $e"
                    }
                } else {
                    println("No auth for writing")
                }
            } catch (e: Exception) {
                println("Error connecting to tag: $e")
                debugMessage += "\n $e"
            } finally {
                try {
                    mfc?.close()
                } catch (e: IOException) {
                    println("Error closing tag: $e")
                    debugMessage += "\n $e"
                }
            }
        }
    }

    private suspend fun readData(tag: Tag?): Array<ByteArray?> = coroutineScope {
        val mfc = MifareClassic.get(tag)
        mfc.connect()
        val cardId = tag?.id
        var data: ByteArray?
        var authA: Boolean
        var authB: Boolean
        val secCount = mfc.sectorCount
        var bCount: Int
        var bIndex: Int

        println("card id: ${ getHexString(cardId, cardId?.size ?: 0) }")
        println("sector count: $secCount")

        val response: Deferred<Array<ByteArray>> = async {
            debugMessage += "\nПолучаю ключи"
            getCardKeys(cardId)
        }
        val cardKeys = response.await()

        val sector10Key = cardKeys[0]
        val sector12Key = cardKeys[1]

        debugMessage += "\nПолучил ключи " +
                "${ getHexString(sector10Key, sector10Key?.size ?: 0) }, " +
                getHexString(sector12Key, sector12Key?.size ?: 0)

        for (j in 0 until secCount) {
            val authKey = when(j) {
                10 -> sector10Key
                12 -> sector12Key
                else -> MifareClassic.KEY_DEFAULT
            }
            authA = mfc.authenticateSectorWithKeyA(j, authKey)
            if (authA) {
                bCount = mfc.getBlockCountInSector(j)
                bIndex = mfc.sectorToBlock(j)
                for (i in 0 until bCount) {
                    data = mfc.readBlock(bIndex)
                    Log.i(TAG, getHexString(data, data?.size ?: 0))
//                        println(getHexString(data, data?.size ?: 0))
                    if (j == 12 && i == 0) {
                        val balanceData = data.take(4).toByteArray()
                        val bigInteger = BigInteger(balanceData)
                        balance = (bigInteger.toFloat() / 100).toString()
                        println(getHexString(data, data.size))
                    }
                    else if (j == 10 && i == 2) {
                        print("block $i ")
                        println(getHexString(data, data?.size ?: 0))
                        name = String(data.copyOfRange(0, data.size - 2))
                    }
                    bIndex++
                }
            } else {
                println("sector $j: Auth failed")
            }
        }
        mfc.close()
        return@coroutineScope arrayOf(cardId, sector10Key, sector12Key)
    }


    fun intToBytes(value: Int): ByteArray {
        val result = ByteArray(4)
        result[0] = (value shr 24).toByte()
        result[1] = (value shr 16).toByte()
        result[2] = (value shr 8).toByte()
        result[3] = value.toByte()
        return result
    }
}

sealed class Screen(val route: String) {
    data object Start : Screen("start")
    data object CheckBalance : Screen("check_balance")
}

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.CheckBalance.route) {
        composable(Screen.Start.route) {
            StartScreen()
        }
        composable(Screen.CheckBalance.route) {
            CheckBalanceScreen(
                username = name,
                cardBalance = balance,
                serverBalance = "0",
                completeWriting = completeWriting,
                isAddingBalance = isAddingBalance,
                onAddingBalanceChange = { isAddingBalance = true; println("is adding balance $isAddingBalance")},
                onNewBalanceChange = { newBalance = it; println("newbalance: $newBalance")},
                service = service,
                onDismiss = {isAddingBalance = false; completeWriting = false}
            )
        }
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

@Preview
@Composable
fun PreviewApp() {
    App()
}