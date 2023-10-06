package com.darekbx.infopigula.domain

import com.darekbx.infopigula.BuildConfig
import com.darekbx.infopigula.model.User
import com.darekbx.infopigula.repository.RemoteRepository
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class UserNotLoggedInException : Exception()

class CurrentUserUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) {
    operator suspend fun invoke(): Result<User> {
        try {
            val currentUser = remoteRepository.currentUser().first()
            with(currentUser.data) {
                val user = User(userId, userEmail, subscriptionPlanName, subscriptionEnd)
                return Result.success(user)
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            if (e is HttpException) {
                if (NOT_AUTHORIZED_CODES.contains(e.code())) {
                    return Result.failure(UserNotLoggedInException())
                }
            }
            return Result.failure(e)
        }
    }

    companion object {
        private val NOT_AUTHORIZED_CODES =
            listOf(HttpURLConnection.HTTP_UNAUTHORIZED, HttpURLConnection.HTTP_FORBIDDEN)
    }
}