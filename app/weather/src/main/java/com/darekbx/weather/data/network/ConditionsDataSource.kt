package com.darekbx.weather.data.network

interface ConditionsDataSource {

    enum class ImageType {
        MAP,
        RAIN,
        STORM,
        PROBABILITIES
    }

    suspend fun getImagesUrls(lat: Double, lng: Double): Map<ImageType, String>
}