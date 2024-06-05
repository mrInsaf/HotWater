package com.example.mynfc.misc

import android.content.Context
import co.yml.charts.common.model.Point
import com.example.mynfc.calculateCRC16Modbus
import com.example.mynfc.ui.voda.VodaUiState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.StateFlow

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

fun jsonStringToList(jsonString: String): List<Map<String, Any>> {
    val gson = Gson()
    val type = object : TypeToken<List<Map<String, Any>>>() {}.type
    return gson.fromJson(jsonString, type)
}

fun retrieveTransactionData(listOfTransactions: List<Map<String, Any>>): List<Map<String, String>> {
    return listOfTransactions.map { transaction ->
        val dateParts = transaction["date"].toString().split(" ")
        val day = dateParts[0]
        val month = when (dateParts[1]) {
            "января" -> "01"
            "февраля" -> "02"
            "марта" -> "03"
            "апреля" -> "04"
            "мая" -> "05"
            "июня" -> "06"
            "июля" -> "07"
            "августа" -> "08"
            "сентября" -> "09"
            "октября" -> "10"
            "ноября" -> "11"
            "декабря" -> "12"
            else -> error("Invalid month")
        }.toString()
        val year = dateParts[2]
        val time = dateParts[3]
        val newCardBalance = transaction["new_card_balance"].toString()
        val value = transaction["value"].toString()

        mapOf(
            "day" to day,
            "month" to month,
            "newCardBalance" to newCardBalance,
            "value" to value,
            "year" to year,
            "time" to time
        )
    }
}

fun createPointsList(transactionsData: List<Map<String, String>>): List<Map<String, String>> {
//    val sortedList = transactionsData.sortedWith(compareBy({ it["month"] }, { it["day"] }))
    val pointsList = mutableListOf<Map<String, String>>()

    transactionsData.forEach { transaction ->
        val day = transaction["day"] ?: ""
        val month = transaction["month"] ?: ""
        val newCardBalance = transaction["newCardBalance"] ?: ""
        val date = "$day.$month"

        pointsList.add(mapOf("date" to date, "newCardBalance" to newCardBalance))
    }

    return pointsList.reversed()
}


fun pointsListToData(pointsList: List<Map<String, String>>): List<Point> {
    return pointsList.mapIndexed { index, point ->
        val newCardBalance = point["newCardBalance"]?.toFloatOrNull()
        Point(x = (index).toFloat(), y = newCardBalance ?: 0f)
    }
}

fun getCurrentLocale(context: Context): String {
    val configuration = context.resources.configuration
    return configuration.locales.get(0).language // Получаем код текущего языка
}


