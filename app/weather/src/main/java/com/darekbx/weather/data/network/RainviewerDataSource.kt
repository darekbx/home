package com.darekbx.weather.data.network

import android.graphics.Bitmap

class RainviewerDataSource: WeatherDataSource {

    override suspend fun getImagesUrls(
        lat: Double,
        lng: Double
    ): Map<WeatherDataSource.ImageType, String> {
        TODO("Not yet implemented")
    }

}