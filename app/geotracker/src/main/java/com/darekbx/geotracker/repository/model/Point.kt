package com.darekbx.geotracker.repository.model

data class Point(
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val speed: Float,
    val altitude: Double
) {

    override fun toString(): String {
        return "$timestamp, ($latitude, $longitude), ${speed}ms, ${altitude}m"
    }
}