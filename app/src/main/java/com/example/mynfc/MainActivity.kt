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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mynfc.ui.theme.MyNFCTheme
import java.io.IOException


class MainActivity : ComponentActivity() {
    private lateinit var mAdapter: NfcAdapter
    private lateinit var mPendingIntent: PendingIntent
    private lateinit var mFilters: Array<IntentFilter>
    private lateinit var mTechLists: Array<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
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

        // Обрабатываем данные тега
        resolveIntent(intent!!)
    }

    private fun resolveIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_TECH_DISCOVERED == action) {
            val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val mfc = MifareClassic.get(tagFromIntent)
            var data: ByteArray?
//            try {

            mfc.connect()
            var authA: Boolean
            var authB: Boolean
            val secCount = mfc.sectorCount
            var bCount: Int
            var bIndex: Int
            println("sector count: $secCount")

            for (j in 0 until secCount) {
                authA = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT)
                if (authA) {
                    bCount = mfc.getBlockCountInSector(j)
                    bIndex = mfc.sectorToBlock(j)
                    for (i in 0 until bCount) {

                        data = mfc.readBlock(bIndex)
                        Log.i(TAG, getHexString(data, data?.size ?: 0))
                        println("sector $j block $i ${ getHexString(data, data?.size ?: 0) }")
                        bIndex++
                    }
                } else {
                    println("sector $j: Auth failed")
                }
            }
//            } catch (e: IOException) {
//                Log.e(TAG, e.localizedMessage)
//                showAlert(3)
//                println("something went wrong")
//            }
        }
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

    private fun showAlert(code: Int) {
        // Ваш код для отображения уведомления об ошибке
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyNFCTheme {
        Greeting("Android")
    }
}