package com.darekbx.geotracker.repository

import com.darekbx.geotracker.repository.entities.PlaceDto
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.RouteDto
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.repository.entities.TrackDto
import com.darekbx.storage.legacy.GeoTrackerHelper
import java.util.Calendar
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val trackDao: TrackDao,
    private val placeDao: PlaceDao,
    private val routeDao: RouteDao,
    private val pointDao: PointDao,
    private val geoTrackerHelper: GeoTrackerHelper?
) : BaseHomeRepository {

    override suspend fun fetchMaxSpeed(): PointDto? {
        return pointDao.fetchMaxSpeed().firstOrNull()
    }

    override suspend fun fetchAllTracks(): List<TrackDto> {
        prepareLegacyStorage()
        return trackDao.fetchAll()
    }

    override suspend fun fetchYearTracks(): List<TrackDto> {
        val startTimestamp = currentYearTimestamp()
        return trackDao.fetchAll(startTimestamp.timeInMillis)
    }

    override suspend fun fetchYearTrackPoints(nthPointsToSkip: Int): Map<Long, List<SimplePointDto>> {
        val startTimestamp = currentYearTimestamp()
        return pointDao
            .fetchAllPoints(startTimestamp.timeInMillis, nthPointsToSkip)
            .groupBy { it.trackId }
    }

    /**
     * Get {current_year}-01-01 00:00:00 date
     */
    private fun currentYearTimestamp(): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.MONTH, 0)
            set(Calendar.DAY_OF_YEAR, 0)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
    }

    private suspend fun prepareLegacyStorage() {
        if (trackDao.countAllTracks() == 0) {
            fillFromLegacyDatabase()
        }
    }

    private suspend fun fillFromLegacyDatabase() {
        trackDao.addAll((geoTrackerHelper?.getTracks() ?: emptyList()).map {
            TrackDto(null, it.label, it.startTimestamp, it.endTimestamp, it.distance)
        })
        placeDao.addAll((geoTrackerHelper?.getPlaces() ?: emptyList()).map {
            PlaceDto(null, it.label, it.latitude, it.longitude, it.timestamp)
        })
        routeDao.addAll((geoTrackerHelper?.getRoutes() ?: emptyList()).map {
            RouteDto(null, it.label, it.url, it.timestamp)
        })
        pointDao.addAll((geoTrackerHelper?.getPoints() ?: emptyList()).map {
            PointDto(null, it.trackId, it.timestamp, it.latitude, it.longitude, it.speed, it.altitude)
        })
    }
}