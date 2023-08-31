package com.darekbx.geotracker.utils

object DateTimeUtils {

    fun getFormattedTime(timeInSeconds: Long): String {
        val minutes = timeInSeconds % 3600 / 60
        val hours = timeInSeconds % 86400 / 3600
        val days = timeInSeconds / 86400

        return "${days.pad()}d ${hours.pad()}h ${minutes.pad()}m"
    }

    private fun Long.pad() = toString().padStart(2, '0')
}