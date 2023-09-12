package com.darekbx.geotracker.repository

import android.util.Log
import com.darekbx.geotracker.repository.entities.PlaceDto
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.RouteDto
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.repository.entities.TrackDto
import com.darekbx.storage.geotracker.TrackPointsDto
import com.darekbx.storage.legacy.GeoTrackerHelper
import java.util.Calendar
import javax.inject.Inject

interface BaseHomeRepository {

    suspend fun fetchAllTracks(): List<TrackDto>

    suspend fun fetchYearTracks(): List<TrackDto>

    suspend fun fetchYearTracks(year: Int): List<TrackPointsDto>

    suspend fun fetchYears(): List<Int>

    suspend fun fetchYearTrackPoints(nthPointsToSkip: Int): Map<Long, List<SimplePointDto>>

    suspend fun fetchMaxSpeed(): PointDto?
}

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

    override suspend fun fetchYearTracks(year: Int): List<TrackPointsDto> {
        val startTimestamp = yearStartTimestamp(year)
        val endTimestamp = yearEndTimestamp(year)
        return trackDao.fetchAll(startTimestamp.timeInMillis, endTimestamp.timeInMillis)
    }

    override suspend fun fetchYearTrackPoints(nthPointsToSkip: Int): Map<Long, List<SimplePointDto>> {
        val startTimestamp = currentYearTimestamp()
        return pointDao
            .fetchAllPoints(startTimestamp.timeInMillis, nthPointsToSkip)
            .groupBy { it.trackId }
    }

    override suspend fun fetchYears(): List<Int> {
        return trackDao.fetchDistinctYears()
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

    private fun yearStartTimestamp(year: Int): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_YEAR, 0)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
    }

    private fun yearEndTimestamp(year: Int): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 31)
            set(Calendar.HOUR_OF_DAY, 24)
            set(Calendar.MINUTE, 60)
            set(Calendar.SECOND, 60)
        }
    }

    private suspend fun prepareLegacyStorage() {
        if (trackDao.countAllTracks() == 0) {
            fillFromLegacyDatabase()
        }
    }

    private suspend fun fillFromLegacyDatabase() {
        Log.v("SIGMA", "fillFromLegacyDatabase")
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