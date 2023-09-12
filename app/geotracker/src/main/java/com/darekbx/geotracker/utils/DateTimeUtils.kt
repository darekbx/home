package com.darekbx.geotracker.utils

import java.sql.Date
import java.text.SimpleDateFormat

object DateTimeUtils {

    val DATE_FORMAT = "yyyy-MM-dd HH:mm"

    val dateFormatter by lazy { SimpleDateFormat(DATE_FORMAT) }

    fun getFormattedTime(timeInSeconds: Long, short: Boolean = false): String {
        val minutes = timeInSeconds % 3600 / 60
        val hours = timeInSeconds % 86400 / 3600
        val days = timeInSeconds / 86400

        if (short) {
            return "${hours.pad()}h ${minutes.pad()}m"
        } else {
            return "${days.pad()}d ${hours.pad()}h ${minutes.pad()}m"
        }
    }

    fun formattedDate(timestamp: Long) = dateFormatter.format(Date(timestamp))

    private fun Long.pad() = toString().padStart(2, '0')
}