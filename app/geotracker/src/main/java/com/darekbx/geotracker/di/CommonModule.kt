package com.darekbx.geotracker.di

import com.darekbx.geotracker.repository.BaseHomeRepository
import com.darekbx.geotracker.repository.HomeRepository
import com.darekbx.geotracker.repository.PlaceDao
import com.darekbx.geotracker.repository.PointDao
import com.darekbx.geotracker.repository.RouteDao
import com.darekbx.geotracker.repository.TrackDao
import com.darekbx.storage.legacy.GeoTrackerHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideBaseHomeRepository(
        trackDao: TrackDao,
        placeDao: PlaceDao,
        routeDao: RouteDao,
        pointDao: PointDao,
        geoTrackerHelper: GeoTrackerHelper?
    ): BaseHomeRepository {
        return HomeRepository(trackDao, placeDao, routeDao, pointDao, geoTrackerHelper)
    }
}
