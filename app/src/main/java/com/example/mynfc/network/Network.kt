package com.example.mynfc.network

import com.example.mynfc.misc.getHexString
import com.example.mynfc.misc.hexStringToByteArray
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


suspend fun getQuery(route: String): JSONObject = coroutineScope {
    val url = "http://188.120.254.122:8000/$route"
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .get()
        .build()
    val response = client.newCall(request).execute()
    val jsonResponse = response.body?.string() ?: ""
    val jsonObject = JSONObject(jsonResponse)
    println(jsonObject)
    return@coroutineScope jsonObject
}

suspend fun postQuery(route: String, jsonBody: String) = coroutineScope {
    val url = "http://188.120.254.122:8000/$route"
    val client = OkHttpClient()
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody = jsonBody.toRequestBody(mediaType)
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()
    val response = client.newCall(request).execute()
    val jsonResponse = response.body?.string() ?: ""
    val jsonObject = JSONObject(jsonResponse)
    println(jsonObject)
    return@coroutineScope jsonObject
}

suspend fun getCard(cardId: ByteArray?): JSONObject = coroutineScope {
    val url = "nfc/hello/"
    val jsonBody = """
                    {
                        "cardId": "${getHexString(cardId, cardId?.size ?: 0)}"
                    }
                """.trimIndent()
    val jsonObject = postQuery(route = url, jsonBody = jsonBody)
    println(jsonObject)
    return@coroutineScope jsonObject
}

suspend fun getCardKeys(jsonObject: JSONObject): Array<ByteArray> = coroutineScope {
    val sector10Key = hexStringToByteArray(jsonObject.getString("sector_10_key"))
    val sector12Key = hexStringToByteArray(jsonObject.getString("sector_12_key"))
    return@coroutineScope arrayOf(sector10Key, sector12Key)
}

suspend fun getServerBalance(jsonObject: JSONObject): String= coroutineScope {
    return@coroutineScope jsonObject.getString("server_balance")
}

//suspend fun updateServerBalanceNetwork(cardId: ByteArray?, newBalance: String) = coroutineScope {
//    println("started updating server balance")
//    val url = "nfc/update-server-balance/"
//    println("newbalance on network: $newBalance")
//    val jsonBody = """
//                    {
//                        "cardId": "${getHexString(cardId, cardId?.size ?: 0)}",
//                        "newBalance": "$newBalance"
//                    }
//                """.trimIndent()
//    val jsonObject = postQuery(route = url, jsonBody = jsonBody)
//
//    if (jsonObject.has("error")) {
//        val errorMessage = jsonObject.getString("error")
//        println("Ошибка: $errorMessage")
//        return@coroutineScope "Что-то пошло не так"
//    } else {
//        val serverBalance = jsonObject.getString("new_server_balance")
//        return@coroutineScope serverBalance
//    }
//}
//
suspend fun createTransactionService(cardId: ByteArray?, value: String): String = coroutineScope {
    println("started creating transaction")
    val url = "transactions/create-transaction-service/"
    val jsonBody = """
                    {
                        "cardId": "${getHexString(cardId, cardId?.size ?: 0)}",
                        "value": "$value"
                    }
                """.trimIndent()
    val jsonObject = postQuery(route = url, jsonBody = jsonBody)

    if (jsonObject.has("error")) {
        val errorMessage = jsonObject.getString("error")
        println("Ошибка: $errorMessage")
        throw Exception("Ошибка: $errorMessage")
    } else {
        return@coroutineScope jsonObject.getString("transaction_id")
    }
}
//
//suspend fun revertTransaction(transactionId: String) = coroutineScope {
//    println("started reverting transaction, transaction id: $transactionId")
//    val url = "transactions/revert-transaction/"
//    val jsonBody = """
//                    {
//                        "transactionId": "$transactionId"
//                    }
//                """.trimIndent()
//    val jsonObject = postQuery(route = url, jsonBody = jsonBody)
//
//    if (jsonObject.has("error")) {
//        val errorMessage = jsonObject.getString("error")
//        println("Ошибка: $errorMessage")
//        throw Exception("Ошибка: $errorMessage")
//    } else {
//        return@coroutineScope
//    }
//}

suspend fun updateServerBalanceNetworkUser(
    cardId: ByteArray?,
    transactionValue: String,
    newBalance: String
): List<String> = coroutineScope {
    println("started updating server balance")
    val url = "transactions/update-server-balance/"
    println("newbalance on network: $newBalance")
    val jsonBody = """
                    {
                        "cardId": "${getHexString(cardId, cardId?.size ?: 0)}",
                        "transactionValue": "$transactionValue"
                    }
                """.trimIndent()
    val jsonObject = postQuery(route = url, jsonBody = jsonBody)

    if (jsonObject.has("error")) {
        val errorMessage = jsonObject.getString("error")
        println("Ошибка: $errorMessage")
        throw Exception("Ошибка: $errorMessage")
    } else {
        val transactionId = jsonObject.getString("transactionId")
        val message = jsonObject.getString("message")
        return@coroutineScope listOf(transactionId, message)
    }
}

suspend fun confirmTransaction(transactionId: String, confirm: Boolean, newCardBalance: String = "") = coroutineScope {
    println("started confirming transaction")
    val url = "transactions/confirm-transaction/"
    val jsonBody = """
                    {
                        "transactionId": "$transactionId",
                        "confirm": "$confirm",
                        "newCardBalance": "$newCardBalance"
                    }
                """.trimIndent()
    val jsonObject = postQuery(route = url, jsonBody = jsonBody)

    if (jsonObject.has("error")) {
        val errorMessage = jsonObject.getString("error")
        println("Ошибка: $errorMessage")
        throw Exception("Ошибка: $errorMessage")
    } else {
        val newServerBalance = jsonObject.getString("new_server_balance")
        return@coroutineScope newServerBalance
    }
}

suspend fun updateServerBalanceNetworkService(
    cardId: ByteArray?,
    newBalance: String
): String = coroutineScope {
    println("started updating server balance")
    val url = "nfc/update-server-balance/"
    println("newbalance on network: $newBalance")
    val jsonBody = """
                    {
                        "cardId": "${getHexString(cardId, cardId?.size ?: 0)}",
                        "newBalance": "$newBalance"
                    }
                """.trimIndent()
    val jsonObject = postQuery(route = url, jsonBody = jsonBody)

    if (jsonObject.has("error")) {
        val errorMessage = jsonObject.getString("error")
        println("Ошибка: $errorMessage")
        throw Exception("Ошибка: $errorMessage")
    } else {
        val serverBalance = jsonObject.getString("new_server_balance")
        return@coroutineScope serverBalance
    }
}


suspend fun getTransactionHistoryByCardId(cardId: ByteArray?) = coroutineScope {
    val url = "transactions/get-transactions/${getHexString(cardId, cardId?.size ?: 0)}"

    val jsonObject = getQuery(route = url)

    if (jsonObject.has("error")) {
        val errorMessage = jsonObject.getString("error")
        println("Ошибка: $errorMessage")
        throw Exception("Ошибка: $errorMessage")
    } else {
        return@coroutineScope jsonObject.getString("transactions")
    }
}