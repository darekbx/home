package com.darekbx.diggpl.di

import com.darekbx.diggpl.BuildConfig
import com.darekbx.diggpl.data.TokenRepository
import com.darekbx.diggpl.data.remote.AuthService
import com.darekbx.diggpl.data.remote.AuthInterceptor
import com.darekbx.diggpl.data.remote.WykopService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    companion object {
        const val WYKOP_BASE_URL = "https://wykop.pl/api/v3/"
    }

    @Provides
    fun provideWykopService(authInterceptor: AuthInterceptor): WykopService {
        return Retrofit.Builder()
            .baseUrl(WYKOP_BASE_URL)
            .client(getRetrofitClient(authInterceptor))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WykopService::class.java)
    }

    @Provides
    fun provideTokenAuthenticator(
        authService: AuthService,
        tokenRepository: TokenRepository
    ): AuthInterceptor {
        return AuthInterceptor(authService, tokenRepository)
    }

    @Provides
    fun provideAuthService(okHttpClient: OkHttpClient): AuthService {
        return Retrofit.Builder()
            .baseUrl(WYKOP_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }

    private fun getRetrofitClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .also { client ->
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
                }
            }.build()
    }
}
