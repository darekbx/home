package com.darekbx.infopigula.repository

import com.darekbx.infopigula.model.NewsResponse
import com.darekbx.infopigula.repository.remote.InfopigulaService
import com.darekbx.infopigula.repository.remote.model.Creator
import com.darekbx.infopigula.repository.remote.model.CurrentUserResponse
import com.darekbx.infopigula.repository.remote.model.LoginResponse
import com.darekbx.infopigula.repository.remote.model.TokenResponse
import com.darekbx.infopigula.repository.remote.model.UserLogin
import com.darekbx.infopigula.repository.remote.model.UserResponse
import javax.inject.Inject

interface RemoteRepository {

    suspend fun token(): TokenResponse

    suspend fun login(userLogin: UserLogin): LoginResponse

    suspend fun getUser(userUid: String): UserResponse

    suspend fun currentUser(): List<CurrentUserResponse>

    suspend fun getNews(): NewsResponse

    suspend fun getCreators(
        page: Int,
        showLastRelease: Int,
    ): NewsResponse

    suspend fun getCreators(): List<Creator>
}

class DefaultRemoteRepository @Inject constructor(
    private val infopigulaService: InfopigulaService
) : RemoteRepository {

    override suspend fun token(): TokenResponse {
        return infopigulaService.token()
    }

    override suspend fun login(userLogin: UserLogin): LoginResponse {
        return infopigulaService.login(userLogin)
    }

    override suspend fun getUser(userUid: String): UserResponse {
        return infopigulaService.getUserInfo(userId = userUid)
    }

    override suspend fun currentUser(): List<CurrentUserResponse> {
        return infopigulaService.getCurrentUser()
    }

    override suspend fun getNews(): NewsResponse {
        return infopigulaService.getNews()
    }

    override suspend fun getCreators(
        page: Int,
        showLastRelease: Int
    ): NewsResponse {
        return infopigulaService.getCreators(page, showLastRelease)
    }

    override suspend fun getCreators(): List<Creator> {
        return infopigulaService.getCreators()
    }
}