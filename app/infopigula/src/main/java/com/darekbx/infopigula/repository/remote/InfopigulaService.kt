package com.darekbx.infopigula.repository.remote

import com.darekbx.infopigula.model.NewsResponse
import com.darekbx.infopigula.repository.remote.model.Creator
import com.darekbx.infopigula.repository.remote.model.CurrentUserResponse
import com.darekbx.infopigula.repository.remote.model.LoginResponse
import com.darekbx.infopigula.repository.remote.model.SubscriptionPlan
import com.darekbx.infopigula.repository.remote.model.TokenResponse
import com.darekbx.infopigula.repository.remote.model.UserLogin
import com.darekbx.infopigula.repository.remote.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InfopigulaService {

    @GET("jwt/token")
    suspend fun token(): TokenResponse

    @POST("user/login?_format=json")
    suspend fun login(@Body userLogin: UserLogin): LoginResponse

    @GET("api/current_user")
    suspend fun getCurrentUser(): List<CurrentUserResponse>

    @GET("user/{user_id}")
    suspend fun getUserInfo(@Path("user_id") userId: String): UserResponse

    @GET("api/v1/creators-app?_format=json")
    suspend fun getCreators(): List<Creator>
    @GET("api/subscription_plans")
    suspend fun getSubscriptionPlans(): SubscriptionPlan

    /**
     * @param groupTargetId id of the news group
     * @param page page number
     * @param showLastRelease 1 when latest news should be fetched
     * @param releaseId pass release id to display other release than latest
     */
    @GET("api/v1/user-dashboard/release")
    suspend fun getNews(): NewsResponse

    @GET("api/v1/news-app/creators?_format=json&keys=&show_favourites_creators=1")
    suspend fun getCreators(
        @Query("page") page: Int,
        @Query("show_last_release") showLastRelease: Int
    ): NewsResponse
}
