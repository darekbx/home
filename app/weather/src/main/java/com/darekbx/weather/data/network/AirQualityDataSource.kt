package com.darekbx.weather.data.network

import com.darekbx.weather.data.network.airly.Installation
import com.darekbx.weather.data.network.airly.Measurements

interface AirQualityDataSource {

    suspend fun readInstallations(
        lat: Double,
        lng: Double,
        maxDistanceKm: Double,
        maxResults: Int
    ): List<Installation>

    suspend fun readMeasurements(
        installationIds: List<Int>,
        measurements: (Measurements) -> Unit
    )
}