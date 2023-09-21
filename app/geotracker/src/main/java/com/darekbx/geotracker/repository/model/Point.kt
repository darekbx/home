package com.darekbx.geotracker.repository.model

data class Point(
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val speed: Float,
    val altitude: Double
) {

    constructor(latitude: Double, longitude: Double) : this(0L, latitude, longitude, 0F, 0.0)

    override fun toString(): String {
        return "$timestamp, ($latitude, $longitude), ${speed}ms, ${altitude}m"
    }
}