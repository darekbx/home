package com.darekbx.notebookcheckreader.repository

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeParser {

    fun getTimestamp(dateTimeString: String): Long {
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.getDefault())
        val offsetDateTime = OffsetDateTime.parse(dateTimeString, formatter)
        return offsetDateTime.toEpochSecond()
    }

    fun formatTimestamp(timestamp: Long): String {
        val instant = Instant.ofEpochSecond(timestamp)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault())
        return localDateTime.format(formatter)
    }
}
