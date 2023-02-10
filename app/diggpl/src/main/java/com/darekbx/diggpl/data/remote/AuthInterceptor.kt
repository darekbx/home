package com.darekbx.diggpl.data.remote

import com.darekbx.diggpl.BuildConfig
import com.darekbx.diggpl.data.TokenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.*
import java.net.HttpURLConnection

class AuthInterceptor(
    private val authService: AuthService,
    private val tokenRepository: TokenRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var accessToken = runBlocking { tokenRepository.accessToken.first() }
        val response = chain.proceed(newRequestWithAccessToken(accessToken, request))

        if (response.code == HttpURLConnection.HTTP_FORBIDDEN) {
            accessToken = blockingTokenRefresh()
            if (accessToken.isBlank()) {

                //sessionManager.logout()

                return response
            }
            tokenRepository.saveAccessToken(accessToken)
            return chain.proceed(newRequestWithAccessToken(accessToken, request))
        }

        return response
    }

    private fun blockingTokenRefresh(): String {
        return runBlocking {
            when (val token = refreshToken()) {
                is ResponseResult.Success -> token.data.token
                else -> ""
            }
        }
    }

    private fun newRequestWithAccessToken(accessToken: String?, request: Request): Request =
        request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

    private suspend fun refreshToken(): ResponseResult<AuthToken> {
        return safeApiCall {
            authService.auth(
                DataWrapper(
                    Data(BuildConfig.DIGG_KEY, BuildConfig.DIGG_SECRET)
                )
            ).data
        }
    }
}
