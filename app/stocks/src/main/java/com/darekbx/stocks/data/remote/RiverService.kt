package com.darekbx.stocks.data.remote

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

data class WaterStateResponse(
    val status: Status
)

data class Status(
    val currentState: CurrentState
)

data class CurrentState(
    val date: String,
    val value: Double
)

interface RiverStateApiService {
    @Headers(
        "Accept: application/json, text/plain, */*",
        "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8",
        "Connection: keep-alive",
        "Cookie: _ga=GA1.1.1766525453.1751881748; _ga_NWX5F9FCRN=GS2.1.s1752041484o5g1t1752041510j34l0h0",
        "Host: hydro-back.imgw.pl",
        "Origin: https://hydro.imgw.pl",
        "Referer: https://hydro.imgw.pl/",
        "Sec-Fetch-Dest: empty",
        "Sec-Fetch-Mode: cors",
        "Sec-Fetch-Site: same-site",
        "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36 OPR/119.0.0.0",
        "sec-ch-ua: \"Chromium\";v=\"134\", \"Not:A-Brand\";v=\"24\", \"Opera\";v=\"119\"",
        "sec-ch-ua-mobile: ?0",
        "sec-ch-ua-platform: \"macOS\""
    )
    @GET("station/hydro/status")
    suspend fun getWaterState(
        @Query("id") id: String
    ): WaterStateResponse

    companion object {
        const val BASE_URL = "https://hydro-back.imgw.pl/"
    }
}
