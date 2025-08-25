package com.darekbx.infopigula.di

import android.content.Context
import com.darekbx.infopigula.BuildConfig
import com.darekbx.infopigula.repository.DefaultRemoteRepository
import com.darekbx.infopigula.repository.DefaultSettingsRepository
import com.darekbx.infopigula.repository.RemoteRepository
import com.darekbx.infopigula.repository.Session
import com.darekbx.infopigula.repository.SettingsRepository
import com.darekbx.infopigula.repository.remote.PigulaAuthInterceptor
import com.darekbx.infopigula.repository.remote.InfopigulaService
import com.darekbx.storage.di.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
        const val API_BASE_URL = "https://infopigula.pl:8443/"
    }

    @Singleton
    @Provides
    fun provideSession(): Session {
        return Session()
    }

    @Singleton
    @Provides
    fun providesRemoteRepository(infopigulaService: InfopigulaService): RemoteRepository {
        return DefaultRemoteRepository(infopigulaService)
    }

    @Singleton
    @Provides
    fun providesSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return DefaultSettingsRepository(context.dataStore)
    }

    @Provides
    fun provideTokenAuthenticator(settingsRepository: SettingsRepository): PigulaAuthInterceptor {
        return PigulaAuthInterceptor(settingsRepository)
    }

    @Provides
    fun provideInfopigulaService(pigulaAuthInterceptor: PigulaAuthInterceptor): InfopigulaService {
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(getRetrofitClient(pigulaAuthInterceptor))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InfopigulaService::class.java)
    }

    private fun getRetrofitClient(pigulaAuthInterceptor: PigulaAuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(pigulaAuthInterceptor)
            .also { client ->
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
                }
            }.build()
    }
}
