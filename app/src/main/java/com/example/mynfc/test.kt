import okhttp3.*
import java.io.IOException

fun main() {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url("https://api.example.com/data")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            println(responseBody)
            // Обработка ответа здесь
        }

        override fun onFailure(call: Call, e: IOException) {
            println("Ошибка при выполнении запроса: ${e.message}")
            // Обработка ошибки здесь
        }
    })
}
