package com.darekbx.weather.data.remote.airly

import com.darekbx.weather.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface AirlyService {

    @Headers("apikey: ${BuildConfig.AIRLY_API_KEY}")
    @GET("v2/installations/nearest")
    suspend fun getInstallations(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("maxDistanceKm") maxDistanceKm: Double,
        @Query("maxResults") maxResults: Int
    ): List<Installation>

    @Headers("apikey: ${BuildConfig.AIRLY_API_KEY}")
    @GET("v2/measurements/installation")
    fun getMeasurement(
        @Query("installationId") installationId: Int
    ): Call<Measurements>

    companion object {
        const val AIRLY_BASE_URL = "https://airapi.airly.eu"
    }
}
