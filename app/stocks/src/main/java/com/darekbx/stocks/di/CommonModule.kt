package com.darekbx.stocks.di

import android.content.Context
import com.darekbx.stocks.data.ArdustocksImport
import com.darekbx.stocks.data.ResponseParser
import com.darekbx.stocks.data.StocksRepository
import com.darekbx.stocks.data.remote.CurrencyService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideResponseParser() = ResponseParser()

    @Provides
    fun provideArdustocksImport(
        @ApplicationContext context: Context,
        stocksRepository: StocksRepository
    ): ArdustocksImport {
        return ArdustocksImport(stocksRepository, context)
    }

    @Provides
    fun provideCurrencyService(okHttpClient: OkHttpClient): CurrencyService {
        return Retrofit.Builder()
            .baseUrl(CurrencyService.CURRENCIES_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(CurrencyService::class.java)
    }
}
