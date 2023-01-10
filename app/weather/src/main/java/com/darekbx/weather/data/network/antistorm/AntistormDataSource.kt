package com.darekbx.weather.data.network.antistorm

import com.darekbx.weather.data.network.ConditionsDataSource
import com.darekbx.weather.data.network.antistorm.AntistormService.Companion.ANTISTORM_BASE_URL
import javax.inject.Inject

class AntistormDataSource @Inject constructor(
    val antistormService: AntistormService
) : ConditionsDataSource {

    override suspend fun getImagesUrls(
        lat: Double,
        lng: Double
    ): Map<ConditionsDataSource.ImageType, String> {

        val radarData = antistormService.getPaths(DirType.TYPE_RADAR.label)
        val stormData = antistormService.getPaths(DirType.TYPE_STORM.label)

        return mapOf(
            ConditionsDataSource.ImageType.MAP to mapUrl(),
            ConditionsDataSource.ImageType.RAIN to rainUrl(radarData),
            ConditionsDataSource.ImageType.PROBABILITIES to probabilitiesUrl(radarData),
            ConditionsDataSource.ImageType.STORM to stormUrl(stormData)
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