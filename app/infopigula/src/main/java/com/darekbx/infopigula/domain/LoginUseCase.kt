package com.darekbx.infopigula.domain

import com.darekbx.infopigula.BuildConfig
import com.darekbx.infopigula.repository.RemoteRepository
import com.darekbx.infopigula.repository.SettingsRepository
import com.darekbx.infopigula.repository.remote.model.UserLogin
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val remoteRepository: RemoteRepository,
) {

    operator suspend fun invoke(email: String, password: String): Result<Boolean> {
        try {
            val userLogin = UserLogin(email, password)
            val result = remoteRepository.login(userLogin)
            settingsRepository.saveAuthCredentials(result.currentUser.uid, result.accessToken)
            return Result.success(true)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            return Result.failure(e)
        }
    }
}
