package com.darekbx.weather.data.remote.rainviewer

import androidx.collection.mutableScatterMapOf
import com.darekbx.weather.data.remote.ConditionsDataSource
import javax.inject.Inject

class RainViewerDataSource @Inject constructor(
    private val rainViewerService: RainViewerService
) : ConditionsDataSource {

    override suspend fun getImagesUrls(
        lat: Double,
        lng: Double
    ): Map<ConditionsDataSource.ImageType, String> {
        val result = mutableScatterMapOf<ConditionsDataSource.ImageType, String>()
        val weatherInfo = rainViewerService.getWeatherMaps()
        weatherInfo.radar.nowcast
            .maxByOrNull { it.time }
            ?.let { newestNowcast ->

                val host = weatherInfo.host
                val nowcast = newestNowcast.path

                result.put(
                    ConditionsDataSource.ImageType.RAIN,
                    "$host$nowcast/$TILE_SIZE/$DEFAULT_ZOOM/$lat/$lng/$MAP_STYLE/$TILE_STYLE.png"
                )
            }

        return result.asMap()
    }

    private companion object {
        const val DEFAULT_ZOOM = 5
        private const val TILE_SIZE = 512
        private const val TILE_STYLE = "0_0"
        private const val MAP_STYLE = 8 // https://www.rainviewer.com/api/color-schemes.html
    }
}
