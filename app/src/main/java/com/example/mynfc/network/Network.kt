package com.example.mynfc.network

import com.example.mynfc.misc.getHexString
import com.example.mynfc.misc.hexStringToByteArray
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

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

suspend fun updateServerBalanceNetwork(cardId: ByteArray?, newBalance: String) = coroutineScope {
    val url = "nfc/update-server-balance/"
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
        return@coroutineScope "Что-то пошло не так"
    } else {
        val serverBalance = jsonObject.getString("new_server_balance")
        return@coroutineScope serverBalance
    }
}