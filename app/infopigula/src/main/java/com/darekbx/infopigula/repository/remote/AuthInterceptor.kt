package com.darekbx.infopigula.repository.remote

import com.darekbx.infopigula.repository.SettingsRepository
import kotlinx.coroutines.runBlocking
import okhttp3.*
import java.net.HttpURLConnection

class AuthInterceptor(
    private val settingsRepository: SettingsRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var accessToken = runBlocking { settingsRepository.accessToken() }
        var csrfToken = runBlocking { settingsRepository.csrfToken() }
        if (accessToken.isNullOrBlank()) {
            return chain.proceed(request)
        }

        val response = chain.proceed(newRequestWithAccessToken(accessToken, csrfToken, request))
        if (response.code == HttpURLConnection.HTTP_FORBIDDEN) {
            runBlocking { settingsRepository.clearCredentials() }
        }

        return response
    }

    private fun newRequestWithAccessToken(accessToken: String?, csrfToken: String?, request: Request): Request =
        request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .header("X-CSRF-Token", csrfToken ?: "")
            .build()
}
