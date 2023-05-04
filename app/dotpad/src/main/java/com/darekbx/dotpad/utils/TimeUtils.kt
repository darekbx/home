package com.darekbx.dotpad.utils

import java.sql.Date
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

object TimeUtils {

    val DATE_FORMAT = "yyyy-MM-dd HH:mm"

    fun formattedDate(createdDate: Long) = dateFormatter.format(Date(createdDate))

    val dateFormatter by lazy { SimpleDateFormat(DATE_FORMAT) }

    fun calculateTimeAgo(createdTime: Long): String {
        val currentTime = System.currentTimeMillis()
        val diff = (currentTime - createdTime)
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "${TimeUnit.MILLISECONDS.toSeconds(diff)}s"
            diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m"
            diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h"
            else -> "${TimeUnit.MILLISECONDS.toDays(diff)}d"
        }
    }

}