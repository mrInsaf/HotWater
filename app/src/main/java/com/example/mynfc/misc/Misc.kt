package com.example.mynfc.misc

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