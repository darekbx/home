package com.darekbx.weather.data.network.rainviewer

import retrofit2.http.GET

interface RainViewerService {

    @GET("/public/weather-maps.json")
    suspend fun getWeatherMaps(): WeatherMap

    companion object {
        const val RAIN_VIEWER_BASE_URL = "https://api.rainviewer.com"
    }
}
