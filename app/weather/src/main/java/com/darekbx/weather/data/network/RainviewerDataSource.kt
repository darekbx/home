package com.darekbx.weather.data.network

import android.graphics.Bitmap

class RainviewerDataSource: WeatherDataSource {

    override suspend fun getRainprediction(lat: Double, lng: Double): Bitmap {
        TODO("Not yet implemented")
    }
}