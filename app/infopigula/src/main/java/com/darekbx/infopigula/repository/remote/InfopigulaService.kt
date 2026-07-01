package com.darekbx.infopigula.repository.remote

import com.darekbx.infopigula.model.NewsResponse
import retrofit2.http.GET

interface InfopigulaService {

    @GET("api/v1/user-dashboard/release")
    suspend fun getNews(): NewsResponse
}
