package com.darekbx.stocks.data

class ResponseParser {

    fun parseResponse(response: String): Double? {
        val start = response.indexOf("cy~")
        val end = response.indexOf("~", start + 3)
        val value = response.substring(start + 3, end)
        return value.toDoubleOrNull()
    }
}