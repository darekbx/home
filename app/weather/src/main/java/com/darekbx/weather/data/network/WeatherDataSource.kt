package com.darekbx.weather.data.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject

interface WeatherDataSource {

    suspend fun getRainprediction(lat: Double, lng: Double): Bitmap
}