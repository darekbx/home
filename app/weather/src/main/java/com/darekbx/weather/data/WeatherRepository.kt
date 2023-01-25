package com.darekbx.weather.data

import com.darekbx.weather.BuildConfig
import com.darekbx.weather.data.remote.AirQualityDataSource
import com.darekbx.weather.data.remote.ConditionsDataSource
import com.darekbx.weather.data.remote.airly.Installation
import com.darekbx.weather.data.remote.airly.Measurements
import com.darekbx.weather.data.remote.antistorm.AntistormDataSource
import com.darekbx.weather.data.remote.rainviewer.RainViewerDataSource
import kotlinx.coroutines.delay
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val antistormDataSource: AntistormDataSource,
    private val rainViewerDataSource: RainViewerDataSource,
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

    suspend fun getImagesUrls(
        useAntistorm: Boolean,
        lat: Double,
        lng: Double
    ): Map<ConditionsDataSource.ImageType, String> {
        // Used to show loading progress
        delay(250)

        return try {
            if (useAntistorm) {
                antistormDataSource.getImagesUrls(0.0, 0.0 /* Not used for Antistorm */)
            } else {
                rainViewerDataSource.getImagesUrls(lat, lng)
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            emptyMap()
        }
    }
}
