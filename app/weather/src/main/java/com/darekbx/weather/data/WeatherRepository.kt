package com.darekbx.weather.data

import com.darekbx.weather.BuildConfig
import com.darekbx.weather.data.network.AirQualityDataSource
import com.darekbx.weather.data.network.ConditionsDataSource
import com.darekbx.weather.data.network.airly.Installation
import com.darekbx.weather.data.network.airly.Measurements
import kotlinx.coroutines.delay
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val conditionsDataSource: ConditionsDataSource,
    private val airQualityDataSource: AirQualityDataSource
) {

    suspend fun readInstallations(
        lat: Double,
        lng: Double,
        maxDistanceKm: Double,
        maxResults: Int
    ): List<Installation> {
        return try {
            airQualityDataSource.readInstallations(lat, lng, maxDistanceKm, maxResults)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            emptyList()
        }
    }

    suspend fun readMeasurements(
        installationIds: List<Int>,
        measurements: (Measurements) -> Unit
    ) {
        try {
            return airQualityDataSource.readMeasurements(installationIds, measurements)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getImagesUrls(): Map<ConditionsDataSource.ImageType, String> {
        // Used to show loading progress
        delay(250)
        return try {
            conditionsDataSource.getImagesUrls(0.0, 0.0)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            emptyMap()
        }
    }
}
