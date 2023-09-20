package com.darekbx.geotracker.repository.model

import com.darekbx.geotracker.utils.DateTimeUtils

data class Track(
    val id: Long,
    val label: String? = null,
    val startTimestamp: Long,
    val endTimestamp: Long? = null,
    val distance: Float? = null,
    val pointsCount: Int
) {

    companion object {
        private const val TRASA_MARK = "Trasa"
    }

    fun filterById(filter: String) = "$id".contains(filter, true)

    fun filterByLabel(filter: String) = "$label".contains(filter, true)

    fun emptyHours() = timespan()?.startsWith("00h") ?: false

    fun hasPoints() = pointsCount > 0

    fun label() = label?.takeIf { it != TRASA_MARK }

    fun timespan() = endTimestamp?.let {
        DateTimeUtils.getFormattedTime((endTimestamp - startTimestamp) / 1000, short = true)
    }

    fun timespanWithSeconds() = endTimestamp?.let {
        DateTimeUtils.getFormattedTime(
            (endTimestamp - startTimestamp) / 1000,
            short = true,
            withSeconds = true
        )
    }

    fun fromattedStartTime() = DateTimeUtils.formattedDate(startTimestamp)
}