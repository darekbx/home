package com.darekbx.diggpl.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WykopService {

    @GET("tags/autocomplete")
    suspend fun tagAutocomplete(
        @Query("query") query: String
    ): ListWrapper<List<TagAutocomplete>>

    @GET("tags/{tagName}/newer")
    suspend fun tagNewCount(
        @Path("tagName") tagName: String,
        @Query("sort") sort: String,
        @Query("date") lastId: String,
    ): DataWrapper<TagNewCount>

    @GET("tags/{tagName}/stream")
    suspend fun tags(
        @Path("tagName") tagName: String,
        @Query("sort") sort: String,
        @Query("page") page: Int
    ): ListWrapper<List<StreamItem>>

    @GET("links")
    suspend fun links(
        @Query("type") type: String,
        @Query("sort") sort: String,
        @Query("page") page: Int
    ): ListWrapper<List<StreamItem>>

    @GET("links/{linkId}")
    suspend fun link(
        @Path("linkId") linkId: Int,
    ): DataWrapper<StreamItem>

    @GET("links/{linkId}/related")
    suspend fun linkRelated(
        @Path("linkId") linkId: Int,
    ): DataWrapper<List<Related>>

    @GET("links/{linkId}/comments")
    suspend fun linkComments(
        @Path("linkId") linkId: Int,
        @Query("page") page: Int
    ): ListWrapper<List<Comment>>

    @GET("entries/{entryId}")
    suspend fun entry(
        @Path("entryId") entryId: Int,
    ): DataWrapper<StreamItem>

    @GET("entries/{entryId}/comments")
    suspend fun entryComments(
        @Path("entryId") entryId: Int,
        @Query("page") page: Int
    ): ListWrapper<List<Comment>>

}
