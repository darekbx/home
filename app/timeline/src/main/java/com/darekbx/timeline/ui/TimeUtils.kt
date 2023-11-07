package com.darekbx.timeline.ui

import java.sql.Date
import java.text.SimpleDateFormat

object TimeUtils {

    val DATE_FORMAT = "yyyy-MM-dd"
    val dateFormatter by lazy { SimpleDateFormat(DATE_FORMAT) }

    fun formattedDate(createdDate: Long) = dateFormatter.format(Date(createdDate))
}
