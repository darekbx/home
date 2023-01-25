package com.darekbx.weather.di

import android.content.Context
import com.darekbx.weather.BuildConfig
import com.darekbx.weather.data.WeatherRepository
import com.darekbx.weather.data.remote.AirQualityDataSource
import com.darekbx.weather.data.remote.ConditionsDataSource
import com.darekbx.weather.data.remote.airly.AirlyDataSource
import com.darekbx.weather.data.remote.airly.AirlyService
import com.darekbx.weather.data.remote.antistorm.AntistormDataSource
import com.darekbx.weather.data.remote.antistorm.AntistormPathsConverter
import com.darekbx.weather.data.remote.antistorm.AntistormService
import com.darekbx.weather.data.remote.rainviewer.RainViewerDataSource
import com.darekbx.weather.data.remote.rainviewer.RainViewerService
import com.darekbx.weather.ui.weather.LocationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideLocationProvider(fusedLocationProviderClient: FusedLocationProviderClient): LocationProvider {
        return LocationProvider(fusedLocationProviderClient)
    }

    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideWeatherRepository(
        antistormDataSource: AntistormDataSource,
        rainViewerDataSource: RainViewerDataSource,
        airQualityDataSource: AirQualityDataSource
    ): WeatherRepository {
        return WeatherRepository(antistormDataSource, rainViewerDataSource, airQualityDataSource)
    }

    @Provides
    fun provideAntistormDataSource(antistormService: AntistormService): ConditionsDataSource {
        return AntistormDataSource(antistormService)
    }

    @Provides
    fun provideRainViewerDataSource(rainViewerService: RainViewerService): RainViewerDataSource {
        return RainViewerDataSource(rainViewerService)
    }

    @Provides
    fun provideAirQualityDataSource(airlyService: AirlyService): AirQualityDataSource {
        return AirlyDataSource(airlyService)
    }

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
    fun provideAntistormService(okHttpClient: OkHttpClient): AntistormService {
        return Retrofit.Builder()
            .baseUrl(AntistormService.ANTISTORM_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(AntistormPathsConverter())
            .build()
            .create(AntistormService::class.java)
    }

    @Provides
    fun provideAirlyService(okHttpClient: OkHttpClient): AirlyService {
        return Retrofit.Builder()
            .baseUrl(AirlyService.AIRLY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AirlyService::class.java)
    }

    @Provides
    fun provideRainViewerService(okHttpClient: OkHttpClient): RainViewerService {
        return Retrofit.Builder()
            .baseUrl(RainViewerService.RAIN_VIEWER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RainViewerService::class.java)
    }
}
