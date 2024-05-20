package com.darekbx.geotracker.di

import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.location.LocationManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.darekbx.geotracker.gpx.GpxReader
import com.darekbx.geotracker.location.LocationCollector
import com.darekbx.geotracker.repository.BaseRepository
import com.darekbx.geotracker.repository.Repository
import com.darekbx.geotracker.repository.PlaceDao
import com.darekbx.geotracker.repository.PointDao
import com.darekbx.geotracker.repository.RouteDao
import com.darekbx.geotracker.repository.SettingsRepository
import com.darekbx.geotracker.repository.TrackDao
import com.darekbx.geotracker.system.BaseLocationManager
import com.darekbx.geotracker.system.DefaultLocationManager
import com.darekbx.storage.legacy.GeoTrackerHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    fun provideGpxReader(): GpxReader {
        return GpxReader()
    }

    @Provides
    fun provideSettingsRepository(dataStore: DataStore<Preferences>): SettingsRepository {
        return SettingsRepository(dataStore)
    }

    @Provides
    fun provideBaseHomeRepository(
        trackDao: TrackDao,
        placeDao: PlaceDao,
        routeDao: RouteDao,
        pointDao: PointDao,
        geoTrackerHelper: GeoTrackerHelper?
    ): BaseRepository {
        return Repository(trackDao, placeDao, routeDao, pointDao, geoTrackerHelper)
    }

    @Provides
    fun provideBaseLocationManager(locationManager: LocationManager): BaseLocationManager {
        return DefaultLocationManager(locationManager)
    }

    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    fun provideLocationService(@ApplicationContext context: Context): LocationManager {
        return context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @Provides
    fun provideLocationCollector(
        locationManager: BaseLocationManager,
        settingsRepository: SettingsRepository
    ): LocationCollector {
        return LocationCollector(locationManager, settingsRepository)
    }
}
