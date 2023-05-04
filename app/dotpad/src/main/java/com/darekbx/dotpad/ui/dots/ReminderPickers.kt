package com.darekbx.dotpad.ui.dots

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun ShowDatePicker(dateCallback: (Int, Int, Int) -> Unit) {
    val now = Calendar.getInstance()
    val picker = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            dateCallback(year, month, dayOfMonth)
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
    )
    picker.show()
}

@Composable
fun ShowTimePicker(timeCallback: (Int, Int) -> Unit) {
    val now = Calendar.getInstance()
    val picker = TimePickerDialog(
        LocalContext.current,
        { _: TimePicker, hour: Int, minute: Int ->
            timeCallback(hour, minute)
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),  true
    )
    picker.show()
}
