package com.darekbx.diggpl.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WykopService {

    @GET("tags/{tagName}/stream")
    suspend fun tags(
        @Path("tagName") tagName: String,
        @Query("page") page: Int
    ): ListWrapper<List<Tag>>
}
