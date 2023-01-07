package com.darekbx.weather.data

import com.darekbx.weather.data.network.WeatherDataSource
import kotlinx.coroutines.delay
import javax.inject.Inject

class WeatherRepository @Inject constructor(val weatherDataSource: WeatherDataSource) {

    suspend fun getImagesUrls(): Map<WeatherDataSource.ImageType, String> {
        delay(250)
        return weatherDataSource.getImagesUrls(0.0, 0.0)
    }
}
