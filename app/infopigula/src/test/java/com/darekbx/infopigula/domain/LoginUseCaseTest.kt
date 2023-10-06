package com.darekbx.infopigula.domain

import com.darekbx.infopigula.repository.RemoteRepository
import com.darekbx.infopigula.repository.SettingsRepository
import com.darekbx.infopigula.repository.remote.model.CurrentUser
import com.darekbx.infopigula.repository.remote.model.CurrentUserResponse
import com.darekbx.infopigula.repository.remote.model.LoginResponse
import com.darekbx.infopigula.repository.remote.model.UserLogin
import com.darekbx.infopigula.repository.remote.model.UserResponse
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class LoginUseCaseTest {

    @Test
    fun `Successfully saved token after login`() = runTest {
        // Given
        var tokenSaved = false
        var credentialsCorrect = false
        val settingsRepository = object : SettingsRepository {
            override suspend fun isDarkMode(): Boolean = false

            override suspend fun clearCredentials() { }

            override suspend fun accessToken(): String? = null

            override suspend fun userUid(): String? = null

            override suspend fun saveAuthCredentials(userUid: String, accessToken: String) {
                tokenSaved = accessToken == "accessToken"
            }

            override suspend fun saveDarkMode(isDarkMode: Boolean) {}
        }

        val remoteRepository = object : RemoteRepository {
            override suspend fun login(userLogin: UserLogin): LoginResponse {
                credentialsCorrect = userLogin.name == "email" && userLogin.pass == "password"
                return LoginResponse(
                    CurrentUser("uid", "name"),
                    "csrfToken",
                    "logoutToken",
                    "accessToken"
                )
            }

            override suspend fun getUser(userUid: String): UserResponse {
                throw IllegalStateException()
            }

            override suspend fun currentUser(): List<CurrentUserResponse> = emptyList()
        }

        // When
        val result = LoginUseCase(settingsRepository, remoteRepository).invoke("email", "password")

        // Then
        assert(tokenSaved)
        assert(credentialsCorrect)
        assert(result.isSuccess)
    }


    @Test
    fun `Failed to login, server throws an exception`() = runTest {
        // Given
        var tokenSaved = false
        var credentialsCorrect = false
        val settingsRepository = object : SettingsRepository {
            override suspend fun isDarkMode(): Boolean = false

            override suspend fun clearCredentials() {}

            override suspend fun accessToken(): String? = null

            override suspend fun userUid(): String? = null

            override suspend fun saveAuthCredentials(userUid: String, accessToken: String) {
                tokenSaved = accessToken == "accessToken"
            }

            override suspend fun saveDarkMode(isDarkMode: Boolean) {}
        }

        val remoteRepository = object : RemoteRepository {
            override suspend fun login(userLogin: UserLogin): LoginResponse {
                credentialsCorrect = userLogin.name == "email" && userLogin.pass == "password"
                throw HttpException(
                    Response.error<LoginResponse>(
                        500, "".toResponseBody("text/plain".toMediaTypeOrNull())
                    )
                )
            }

            override suspend fun getUser(userUid: String): UserResponse {
                throw IllegalStateException()
            }

            override suspend fun currentUser(): List<CurrentUserResponse> = emptyList()
        }

        // When
        val result = LoginUseCase(settingsRepository, remoteRepository).invoke("email", "password")

        // Then
        assertTrue(credentialsCorrect)
        assertFalse(tokenSaved)
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is HttpException)
    }
}