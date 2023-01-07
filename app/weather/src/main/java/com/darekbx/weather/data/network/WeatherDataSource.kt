package com.darekbx.weather.data.network

interface WeatherDataSource {

    enum class ImageType {
        MAP,
        RAIN,
        STORM,
        PROBABILITIES
    }

    suspend fun getImagesUrls(lat: Double, lng: Double): Map<ImageType, String>
}