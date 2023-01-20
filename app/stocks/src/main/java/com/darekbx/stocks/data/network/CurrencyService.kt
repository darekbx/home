package com.darekbx.stocks.data.network

import com.darekbx.stocks.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {

    @GET("cmp")
    suspend fun getCurrencyInfo(@Query("q") query: String): String

    companion object {
        const val CURRENCIES_BASE_URL = BuildConfig.CURRENCIES_BASE_URL
    }
}
