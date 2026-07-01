package com.darekbx.infopigula.di

import com.darekbx.infopigula.BuildConfig
import com.darekbx.infopigula.repository.DefaultRemoteRepository
import com.darekbx.infopigula.repository.RemoteRepository
import com.darekbx.infopigula.repository.remote.InfopigulaService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    companion object {
        const val API_BASE_URL = "https://infopigula.pl/"
    }

    @Singleton
    @Provides
    fun providesRemoteRepository(infopigulaService: InfopigulaService): RemoteRepository {
        return DefaultRemoteRepository(infopigulaService)
    }

    @Provides
    fun provideInfopigulaService(): InfopigulaService {
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(getRetrofitClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InfopigulaService::class.java)
    }

    private fun getRetrofitClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .also { client ->
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
                }
            }.build()
    }
}
