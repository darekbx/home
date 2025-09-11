package com.darekbx.spreadsheet.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimestampFormatter {

    companion object {
        private const val DATE_PATTERN: String = "yyyy-MM-dd HH:mm:ss"
        @SuppressLint("ConstantLocale")
        private val dateFormat: SimpleDateFormat = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())

        fun formatToDate(timestamp: Long): String {
            return dateFormat.format(Date(timestamp))
        }
    }
}
