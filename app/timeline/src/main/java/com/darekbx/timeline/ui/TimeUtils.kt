package com.darekbx.timeline.ui

import java.sql.Date
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId

data class YearDay(val year: Int, val day: Int)

object TimeUtils {

    val DATE_FORMAT = "yyyy-MM-dd"
    val dateFormatter by lazy { SimpleDateFormat(DATE_FORMAT) }

    fun formattedDate(createdDate: Long) = dateFormatter.format(Date(createdDate))

    fun extractYearFromTimestamp(timestamp: Long): Int {
        val instant = Instant.ofEpochMilli(timestamp)
        val zoneId = ZoneId.of("UTC") // Change to your desired time zone
        val zonedDateTime = instant.atZone(zoneId)
        return zonedDateTime.year
    }

    fun extractDayOfYearFromTimestamp(timestamp: Long): YearDay {
        val instant = Instant.ofEpochMilli(timestamp)
        val zoneId = ZoneId.of("UTC") // Change to your desired time zone
        val zonedDateTime = instant.atZone(zoneId)
        return YearDay(zonedDateTime.year, zonedDateTime.dayOfYear)
    }

}
