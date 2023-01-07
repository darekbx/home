package com.darekbx.weather.data.network.antistorm

import com.darekbx.weather.data.network.WeatherDataSource
import com.darekbx.weather.data.network.antistorm.AntistormService.Companion.ANTISTORM_BASE_URL
import javax.inject.Inject

class AntistormDataSource @Inject constructor(
    val antistormService: AntistormService
) : WeatherDataSource {

    override suspend fun getImagesUrls(
        lat: Double,
        lng: Double
    ): Map<WeatherDataSource.ImageType, String> {

        val radarData = antistormService.getPaths(DirType.TYPE_RADAR.label)
        val stormData = antistormService.getPaths(DirType.TYPE_STORM.label)

        return mapOf(
            WeatherDataSource.ImageType.MAP to mapUrl(),
            WeatherDataSource.ImageType.RAIN to rainUrl(radarData),
            WeatherDataSource.ImageType.PROBABILITIES to probabilitiesUrl(radarData),
            WeatherDataSource.ImageType.STORM to stormUrl(stormData)
        )
    }

    private fun mapUrl(): String {
        return "$ANTISTORM_BASE_URL/map/final-map.png"
    }

    private fun rainUrl(paths: Paths): String {
        val fileFront = paths[PathsType.FILES_FRONT_NAME]?.first()
        return "$ANTISTORM_BASE_URL/visualPhenom/$fileFront-radar-visualPhenomenon.png"
    }

    private fun stormUrl(paths: Paths): String {
        val fileFront = paths[PathsType.FILES_FRONT_NAME]?.first()
        return "$ANTISTORM_BASE_URL/visualPhenom/$fileFront-storm-visualPhenomenon.png"
    }

    private fun probabilitiesUrl(paths: Paths): String {
        val dir = paths[PathsType.DIRS_NAME]?.first()
        val file = paths[PathsType.FILES_NAME]?.first()
        return "$ANTISTORM_BASE_URL/archive/$dir/$file-radar-probabilitiesImg.png"
    }
}