package com.example.mynfc

import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentFilter.MalformedMimeTypeException
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp

import com.example.mynfc.ui.theme.MyNFCTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.math.BigInteger
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.coroutines.EmptyCoroutineContext

var balance by mutableStateOf("")
var name by mutableStateOf("")
var isAddingBalance by  mutableStateOf(false)
var newBalance by mutableStateOf("0")
var debugMessage by mutableStateOf("Начальное сообщение")

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
                    MainPage()
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
        }
    }

    private suspend fun resolveIntent(intent: Intent, writeBalance: Boolean) = coroutineScope {
        val action = intent.action
        if (NfcAdapter.ACTION_TECH_DISCOVERED == action) {
            val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (writeBalance) {
                writeBalance(tagFromIntent)
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

    private fun hexStringToByteArray(hex: String): ByteArray {
        val byteArray = ByteArray(hex.length / 2)
        for (i in hex.indices step 2) {
            val byte = hex.substring(i, i + 2).toInt(16).toByte()
            byteArray[i / 2] = byte
        }
        return byteArray
    }

    private suspend fun readData(tag: Tag?) = coroutineScope {
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

        val response: Deferred<Array<String>> = async {
            debugMessage += "\nПолучаю ключи"
            get_keys(cardId)
        }
        val keysArray = response.await()

        val sector10Key = hexStringToByteArray(keysArray[0])
        val sector12Key = hexStringToByteArray(keysArray[1])
        debugMessage += "\nПолучил ключи " +
                "${ getHexString(sector10Key, sector10Key?.size ?: 0) }, " +
                getHexString(sector12Key, sector12Key?.size ?: 0)

        for (j in 0 until secCount) {
            try {
                val authKey = when (j) {
                    10 -> sector10Key
                    12 -> sector12Key
                    else -> MifareClassic.KEY_DEFAULT
                }
                try{
                    authA = mfc.authenticateSectorWithKeyA(j, authKey)
                    if (authA) {
                        debugMessage += "Сектор $j авторизовано\n"
                        bCount = mfc.getBlockCountInSector(j)
                        bIndex = mfc.sectorToBlock(j)
                        for (i in 0 until bCount) {

                            data = mfc.readBlock(bIndex)
                            Log.i(TAG, getHexString(data, data?.size ?: 0))
//                        println(getHexString(data, data?.size ?: 0))
                            if (j == 12 && i == 1) {
                                val balanceData = data.take(4).toByteArray()
                                val bigInteger = BigInteger(balanceData)
                                balance = (bigInteger.toFloat() / 100).toString()
                                debugMessage += "Полученный баланс $balance\n"
                                println(getHexString(data, data.size))
                            } else if (j == 10 && i == 2) {
                                print("block $i ")
                                println(getHexString(data, data?.size ?: 0))
                                name = String(data)
                                debugMessage += "Полученное имя $name\n"
                            }
                            bIndex++
                        }
                    } else {
                        println("sector $j: Auth failed")
                        debugMessage += "Сектор $j НЕ авторизовано"
                    }
                }
                catch (e: Exception) {
                    debugMessage += e
                }

            }
            catch (e: Exception){
                println(e)
                debugMessage += e
            }


        }
        mfc.close()
        return@coroutineScope arrayOf(cardId, sector10Key, sector12Key)
    }

    suspend fun get_keys(cardId: ByteArray?): Array<String> = coroutineScope {
        val url = "http://188.120.254.122:8000/nfc/hello/"
        val client = OkHttpClient()
        val jsonBody = """
                    {
                        "cardId": "${getHexString(cardId, cardId?.size ?: 0)}"
                    }
                """.trimIndent()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        val response = client.newCall(request).execute()
        val jsonResponse = response.body?.string() ?: ""
        val jsonObject = JSONObject(jsonResponse)


        val sector10Key = jsonObject.getString("sector_10_key")
        val sector12Key = jsonObject.getString("sector_12_key")
        return@coroutineScope arrayOf(sector10Key, sector12Key)
    }


    fun intToBytes(value: Int): ByteArray {
        val result = ByteArray(4)
        result[0] = (value shr 24).toByte()
        result[1] = (value shr 16).toByte()
        result[2] = (value shr 8).toByte()
        result[3] = value.toByte()
        return result
    }

    private fun getHexString(bytes: ByteArray?, length: Int): String {
        val hexString = StringBuilder()
        bytes?.let {
            for (i in 0 until length) {
                val hex = Integer.toHexString(0xFF and it[i].toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
        }
        return hexString.toString()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddBalanceInput(onBalanceAdded: (Int) -> Unit) {
    var inputValue by remember { mutableStateOf(0) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Request focus when this composable is first added to the composition
    val focusRequester = remember { FocusRequester() }
    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose { }
    }

    OutlinedTextField(
        value = inputValue.toString(),
        onValueChange = {
            inputValue = it.toIntOrNull() ?: 0
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        label = { Text("Enter amount") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Button to submit the input
    Button(
        onClick = {
            onBalanceAdded(inputValue)
        }
    ) {
        Text("Add")
    }

    // Close the keyboard when the input is submitted
    DisposableEffect(Unit) {
        keyboardController?.show()
        onDispose {
            keyboardController?.hide()
        }
    }
}

@Composable
fun MainPage(modifier: Modifier = Modifier) {
    var balanceValue by remember { mutableStateOf(0) }
    var textState by remember { mutableStateOf("") }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.color5))
            .verticalScroll(rememberScrollState())
    ) {
        Column {
            Column {
                Text(
                    text = "Добрый день,",
                    color = colorResource(id = R.color.color1),
                    modifier = modifier
                )
                Text(
                    text = name,
                    color = colorResource(id = R.color.color1),
                    fontSize = 24.sp,
                    modifier = modifier
                )
            }
            Spacer(modifier = modifier.size(48.dp))
            Column (
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Баланс",
                    color = colorResource(id = R.color.color1),
                    textAlign = TextAlign.Start,
                    modifier = modifier
                )
                Text(
                    text = "$balance рублей",
                    color = colorResource(id = R.color.color1),
                    textAlign = TextAlign.Start,
                    fontSize = 24.sp,
                    modifier = modifier
                )
                Text(
                    text = debugMessage,
                    color = colorResource(id = R.color.color1),
                )
            }
            Spacer(modifier = modifier.size(48.dp))
        }

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .width(200.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Пополнить баланс")
                Checkbox(
                    checked = isAddingBalance,
                    onCheckedChange = { isAddingBalance = !isAddingBalance })
            }

            TextField(
                value = textState,
                onValueChange = { textState = it; newBalance = it},
                enabled = isAddingBalance,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                placeholder = { Text(text = "макс. 300") }
            )
//            Button(
//                onClick = { newBalance = textState; println(newBalance) },
//                enabled = (textState != null)
//            ) {
//                Text(text = "Пополнить")
//            }

        }
    }
}


@Preview
@Composable
fun MainPagePreview() {
    MainPage()
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
