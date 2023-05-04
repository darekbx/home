package com.darekbx.dotpad.viewmodel

import androidx.compose.ui.graphics.Color
import android.graphics.Color as GColor
import com.darekbx.dotpad.repository.local.entities.DotDto
import com.darekbx.dotpad.ui.dots.Dot
import com.darekbx.dotpad.ui.dots.DotColor
import com.darekbx.dotpad.ui.dots.DotSize

fun DotDto.toDot(): Dot {
    val colorWrapper = Color(color)
    return Dot(
        id,
        text,
        positionX.toFloat(),
        positionY.toFloat(),
        DotSize.values().find { it.size == size },
        DotColor(colorWrapper.red, colorWrapper.green, colorWrapper.blue),
        createdDate,
        isArchived,
        isSticked,
        reminder?.takeIf { it > 0 },
        calendarEventId,
        calendarReminderId
    )
}

fun Dot.toDotDto(): DotDto {
    return DotDto(
        id,
        text,
        size!!.size,
        GColor.argb(1F, color!!.r, color!!.g, color!!.b),
        x.toInt(),
        y.toInt(),
        createdDate,
        isArchived,
        isSticked,
        reminder,
        calendarEventId,
        calendarReminderId
    )
}