package com.darekbx.diggpl.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("auth")
    suspend fun auth(@Body data: DataWrapper<Data>): DataWrapper<AuthToken>
}
