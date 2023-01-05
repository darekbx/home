package com.darekbx.weather.di

import com.darekbx.weather.BuildConfig
import com.darekbx.weather.data.network.antistorm.AntistormPathsConverted
import com.darekbx.weather.data.network.antistorm.AntistormService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            }
        )
        .build()

    @Provides
    fun provideAntistormPaths(okHttpClient: OkHttpClient): AntistormService {
        return Retrofit.Builder()
            .baseUrl(AntistormService.ANTISTORM_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(AntistormPathsConverted())
            .build()
            .create(AntistormService::class.java)
    }
}
