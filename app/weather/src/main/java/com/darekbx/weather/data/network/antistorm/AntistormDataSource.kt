package com.darekbx.weather.data.network.antistorm

import android.graphics.Bitmap
import com.darekbx.weather.data.network.WeatherDataSource

class AntistormDataSource: WeatherDataSource {

    override suspend fun getRainprediction(lat: Double, lng: Double): Bitmap {
        TODO("Not yet implemented")
    }
}