package com.darekbx.words.utils

import java.sql.Date
import java.text.SimpleDateFormat

object DateTimeUtils {

    val DATE_FORMAT = "yyyy-MM-dd"

    val dateFormatter by lazy { SimpleDateFormat(DATE_FORMAT) }

    fun formattedDate(timestamp: Long) = dateFormatter.format(Date(timestamp))
}