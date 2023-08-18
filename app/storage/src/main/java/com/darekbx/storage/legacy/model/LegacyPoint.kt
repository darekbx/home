package com.darekbx.storage.legacy.model

data class LegacyPoint(
    val trackId: Long,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val speed: Float,
    val altitude: Double
)
