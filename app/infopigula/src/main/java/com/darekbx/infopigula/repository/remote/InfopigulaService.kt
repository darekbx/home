package com.darekbx.infopigula.repository.remote

import com.darekbx.infopigula.repository.remote.model.CreatorsResponse
import com.darekbx.infopigula.repository.remote.model.CurrentUserResponse
import com.darekbx.infopigula.repository.remote.model.LoginResponse
import com.darekbx.infopigula.repository.remote.model.NewsResponse
import com.darekbx.infopigula.repository.remote.model.SubscriptionPlan
import com.darekbx.infopigula.repository.remote.model.UserLogin
import com.darekbx.infopigula.repository.remote.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InfopigulaService {

    @POST("user/login?_format=json")
    suspend fun login(@Body userLogin: UserLogin): LoginResponse

    @GET("api/current_user")
    suspend fun getCurrentUser(): List<CurrentUserResponse>

    @GET("user/{user_id}")
    suspend fun getUserInfo(@Path("user_id") userId: String): UserResponse

    @GET("api/v1/creators-app")
    suspend fun getCreators(): CreatorsResponse

    @GET("api/subscription_plans")
    suspend fun getSubscriptionPlans(): SubscriptionPlan

    /**
     * @param groupTargetId id of the news group
     * @param page page number
     * @param showLastRelease 1 when latest news should be fetched
     * @param releaseId pass release id to display other release than latest
     */
    @GET("api/v1/news-app")
    suspend fun getNews(
        @Query("group_target_id") groupTargetId: Int,
        @Query("page") page: Int,
        @Query("show_last_release") showLastRelease: Int,
        @Query("release_nids") releaseId: Int? = null
    ): NewsResponse

}
