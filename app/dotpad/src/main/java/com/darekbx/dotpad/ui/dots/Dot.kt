package com.darekbx.dotpad.ui.dots

import androidx.compose.ui.graphics.Color
import android.graphics.Color as GColor
import java.io.Serializable
import java.util.*


data class Dot(
    var id: Long? = null,
    var text: String,
    var x: Float,
    var y: Float,
    var size: DotSize? = null,
    var color: DotColor? = null,
    var createdDate: Long = System.currentTimeMillis(),
    var isArchived: Boolean = false,
    var isSticked: Boolean = false,
    var reminder: Long? = null,
    var calendarEventId: Long? = null,
    var calendarReminderId: Long? = null
) {

    val isNew = id == null

    val reminderCalendar = reminder?.let {
        Calendar.getInstance().apply { setTimeInMillis(it) }
    }

    fun hasReminder(): Boolean {
        return (reminder ?: 0L) > 0L
    }

    fun requireSize(): DotSize = size ?: throw IllegalStateException("Size is null")

    fun requireColor(): DotColor = color ?: throw IllegalStateException("Color is null")
}

enum class DotSize(val size: Int, val sizeName: String) {
    SMALL(5, "S"),
    MEDIUM(6, "M"),
    LARGE(8, "L"),
    HUGE(10, "XL")
}

class DotColor(val r: Float, val g: Float, val b: Float) : Serializable {

    fun equalsColor(other: DotColor?): Boolean {
        if (other == null) return false
        return other.r == r && other.g == g && other.b == b
    }
}

fun Color.toDotColor(): DotColor = DotColor(red, green, blue)
fun DotColor.toColor(): Color = Color(r, g, b)
fun DotColor.toIntColor(): Int = GColor.argb(1F, r, g, b)
