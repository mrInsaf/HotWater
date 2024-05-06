package com.example.mynfc.network

import com.example.mynfc.misc.getHexString
import com.example.mynfc.misc.hexStringToByteArray
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

suspend fun getCard(cardId: ByteArray?): JSONObject = coroutineScope {
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
    println(jsonObject)
    return@coroutineScope jsonObject
}

suspend fun getCardKeys(cardId: ByteArray?): Array<ByteArray> = coroutineScope {
    val jsonObject = getCard(cardId)
    val sector10Key = hexStringToByteArray(jsonObject.getString("sector_10_key"))
    val sector12Key = hexStringToByteArray(jsonObject.getString("sector_12_key"))

    return@coroutineScope arrayOf(sector10Key, sector12Key)
}