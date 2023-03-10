package com.darekbx.hejto.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HejtoService {

    @GET("communities")
    suspend fun getCommunities(
        @Query("page") page: Int,
        @Query("orderBy") orderBy: String
    ): ResponseWrapper<Community>

    @GET("tags")
    suspend fun getTags(
        @Query("page") page: Int,
        @Query("limit") limit: Int = PAGE_SIZE
    ): ResponseWrapper<Tag>

    @GET("tags/{name}")
    suspend fun getTag(
        @Path("name") name: String
    ): Tag

    @GET("posts")
    suspend fun getPosts(
        @Query("period") period: String?,
        @Query("orderBy") orderBy: String?,
        @Query("community") community: String? = null,
        @Query("type[]") type: List<String>? = null,
        @Query("tags[]") tags: List<String>? = null,
        @Query("page") page: Int
    ): ResponseWrapper<PostDetails>

    @GET("posts/{slug}")
    suspend fun getPostDetails(
        @Path("slug") slug: String
    ): PostDetails

    @GET("posts/{slug}/comments")
    suspend fun getPostComments(
        @Path("slug") slug: String,
        @Query("page") page: Int
    ): ResponseWrapper<PostComment>

    companion object {
        const val HEJTO_BASE_URL = "https://api.hejto.pl"
        const val PAGE_SIZE = 20
    }
}
