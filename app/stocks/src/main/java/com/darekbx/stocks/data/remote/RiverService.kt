package com.darekbx.stocks.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class WaterStateResponse(
    val operational: List<OperationalData>
)

data class OperationalData(
    val state: String,
    val date: String,
    val value: Double
)

interface RiverStateApiService {
    @GET("station/home/waterstate")
    suspend fun getWaterState(
        @Query("id") id: String,
        @Query("hoursInterval") hoursInterval: Int
    ): WaterStateResponse

    companion object {
        const val BASE_URL = "https://hydro-back.imgw.pl/"
    }
}
