package com.darekbx.geotracker.repository.model

import com.darekbx.geotracker.utils.DateTimeUtils
import org.osmdroid.util.GeoPoint

data class PlaceToVisit(
    val id: Long,
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
) {

    fun formattedTimestamp() = DateTimeUtils.formattedDate(timestamp)

    fun location() = GeoPoint(latitude, longitude)
}