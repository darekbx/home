package com.darekbx.geotracker.repository.model

data class SummaryWrapper(
    val summary: Summary,
    val yearSummary: Summary
)

data class Summary(
    val distance: Double,
    val time: Long,
    val tripsCount: Int
)

data class YearSummary(
    val year: Int,
    val distance: Double,
    val time: Long,
    val tripsCount: Int,
    val daysOnBike: Int,
    val longestTrip: Double,
    val maxDayDistance: Double
)
