package com.example.mynfc.misc

import android.nfc.tech.MifareClassic
import com.example.mynfc.calculateCRC16Modbus
import com.example.mynfc.ui.voda.VodaUiState
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

fun getHexString(bytes: ByteArray?, length: Int): String {
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

fun hexStringToByteArray(hex: String): ByteArray {
    val byteArray = ByteArray(hex.length / 2)
    for (i in hex.indices step 2) {
        val byte = hex.substring(i, i + 2).toInt(16).toByte()
        byteArray[i / 2] = byte
    }
    return byteArray
}
 fun calculateResultBytes(floatBalance: Float): ByteArray {
    val intBalance = floatBalance.toInt()
    val bytesBalance = intToBytes(intBalance)
    var resultBytes = ByteArray(14)
    bytesBalance.copyInto(resultBytes, startIndex = 0)
    val crc = calculateCRC16Modbus(resultBytes)
    val bytesCrc = intToBytes(crc).sliceArray(2 until 4).reversedArray()
    println("crc len: ${bytesCrc.size}")
    resultBytes += bytesCrc
    return resultBytes
}

fun intToBytes(value: Int): ByteArray {
    val result = ByteArray(4)
    result[0] = (value shr 24).toByte()
    result[1] = (value shr 16).toByte()
    result[2] = (value shr 8).toByte()
    result[3] = value.toByte()
    return result
}

fun calculateFloatBalance(uiState: StateFlow<VodaUiState>): Float {
    val floatBalance = when {
        uiState.value.isUpdatingServerBalance -> {
            uiState.value.toCardValue.toFloat()
        }
        uiState.value.isUpdatingCardBalance -> {
            uiState.value.toCardValue.toFloat()
        }
        else -> uiState.value.newBalance.toFloat()
    } * 100
    return floatBalance
}

