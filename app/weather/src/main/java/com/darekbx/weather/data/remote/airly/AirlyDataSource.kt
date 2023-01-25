package com.darekbx.weather.data.remote.airly

import com.darekbx.weather.data.remote.AirQualityDataSource
import okhttp3.Response
import java.io.IOException

class AirlyDataSource(val airlyService: AirlyService) : AirQualityDataSource {

    override suspend fun readInstallations(
        lat: Double,
        lng: Double,
        maxDistanceKm: Double,
        maxResults: Int
    ): List<Installation> {
        return airlyService.getInstallations(lat, lng, maxDistanceKm, maxResults)
    }

    override suspend fun readMeasurements(
        installationIds: List<Int>,
        measurementsCallback: (Measurements) -> Unit
    ) {
        installationIds.forEach { id ->
            val response = airlyService.getMeasurement(id).execute()
            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code()}")
            }
            val measurement = response.body()
                ?: throw IllegalStateException("Response is empty")
            val rateLimits = extractRateLimits(response.raw())
            measurement.installationId = id
            if (!rateLimits.isEmpty) {
                measurement.rateLimits = rateLimits
            }
            measurementsCallback(measurement)
        }
    }

    private fun extractRateLimits(response: Response): RateLimits {
        return RateLimits(
            dayLimit = response.headers[X_RATELIMIT_LIMIT_DAY]?.toIntOrNull() ?: -1,
            dayRemaining = response.headers[X_RATELIMIT_REMAINING_DAY]?.toIntOrNull() ?: -1,
            minuteLimit = response.headers[X_RATELIMIT_LIMIT_MINUTE]?.toIntOrNull() ?: -1,
            minuteRemaining = response.headers[X_RATELIMIT_REMAINING_MINUTE]?.toIntOrNull() ?: -1
        )
    }

    private companion object {
        const val X_RATELIMIT_LIMIT_DAY = "x-ratelimit-limit-day"
        const val X_RATELIMIT_REMAINING_DAY = "x-ratelimit-remaining-day"
        const val X_RATELIMIT_LIMIT_MINUTE = "x-ratelimit-limit-minute"
        const val X_RATELIMIT_REMAINING_MINUTE = "x-ratelimit-remaining-minute"
    }
}
