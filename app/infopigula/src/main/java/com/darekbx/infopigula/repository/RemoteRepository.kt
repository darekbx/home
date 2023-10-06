package com.darekbx.infopigula.repository

import com.darekbx.infopigula.repository.remote.InfopigulaService
import com.darekbx.infopigula.repository.remote.model.CurrentUserResponse
import com.darekbx.infopigula.repository.remote.model.LoginResponse
import com.darekbx.infopigula.repository.remote.model.UserLogin
import com.darekbx.infopigula.repository.remote.model.UserResponse
import javax.inject.Inject

interface RemoteRepository {

    suspend fun login(userLogin: UserLogin): LoginResponse

    suspend fun getUser(userUid: String): UserResponse

    suspend fun currentUser(): List<CurrentUserResponse>
}

class DefaultRemoteRepository @Inject constructor(
    private val infopigulaService: InfopigulaService
) : RemoteRepository {

    override suspend fun login(userLogin: UserLogin): LoginResponse {
        return infopigulaService.login(userLogin)
    }

    override suspend fun getUser(userUid: String): UserResponse {
        return infopigulaService.getUserInfo(userId = userUid)
    }

    override suspend fun currentUser(): List<CurrentUserResponse> {
        return infopigulaService.getCurrentUser()
    }

}