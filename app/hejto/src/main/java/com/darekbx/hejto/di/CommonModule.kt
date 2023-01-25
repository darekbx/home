package com.darekbx.hejto.di

import com.darekbx.hejto.data.remote.HejtoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideHejtoService(okHttpClient: OkHttpClient): HejtoService {
        return Retrofit.Builder()
            .baseUrl(HejtoService.HEJTO_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HejtoService::class.java)
    }
}
