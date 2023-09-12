package com.darekbx.geotracker.repository.model

data class ActivityData(val dayOfYear: Int, val distances: List<Double>) {

    fun sumDistance() = distances.sum()
}