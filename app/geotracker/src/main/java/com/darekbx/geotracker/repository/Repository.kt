package com.darekbx.geotracker.repository

import com.darekbx.geotracker.repository.entities.PlaceDto
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.RouteDto
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.repository.entities.TrackDto
import com.darekbx.storage.geotracker.TrackPointsDto
import com.darekbx.storage.legacy.GeoTrackerHelper
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

interface BaseRepository {

    suspend fun fetchAllTracks(): List<TrackDto>

    suspend fun fetchYearTracks(): List<TrackDto>

    suspend fun fetchYearTracks(year: Int): List<TrackPointsDto>

    suspend fun fetchYearTracksStatistics(year: Int): List<TrackDto>

    suspend fun fetchYears(): List<Int>

    suspend fun fetchYearTrackPoints(year: Int?, nthPointsToSkip: Int): Map<Long, List<SimplePointDto>>

    suspend fun fetchAllTrackPoints(nthPointsToSkip: Int): List<List<SimplePointDto>>

    suspend fun fetchMaxSpeed(): PointDto?

    suspend fun fetchUnFinishedTracks(): List<TrackDto>

    suspend fun add(trackDto: TrackDto): Long

    suspend fun add(pointDto: PointDto): Long

    suspend fun add(pointDtos: List<PointDto>)

    suspend fun add(placeDto: PlaceDto)

    suspend fun update(trackId: Long, endTimestamp: Long)

    suspend fun fetch(trackId: Long): TrackDto?

    suspend fun fetchTrackPoints(trackId: Long): List<PointDto>

    suspend fun deleteTrack(trackId: Long)

    suspend fun deleteAllPoints(trackId: Long)

    fun fetchLivePoints(): Flow<List<PointDto>>

    fun fetchLastPoint(): PointDto

    fun fetchLiveTrack(): Flow<TrackDto?>

    suspend fun appendDistance(trackId: Long, distance: Float)

    suspend fun updateTrack(trackId: Long, endTimestamp: Long, label: String?)

    suspend fun saveLabel(trackId: Long, label: String?)

    suspend fun fetchPlacesToVisit(): List<PlaceDto>

    suspend fun deletePlaceToVisit(id: Long)

    suspend fun countPlacesToVisit(): Int

    suspend fun countPoints(): Long

    /**
     * WARNING
     * Deletes all data and restores from legacy db
     */
    suspend fun restoreLegacyDb()
}

class Repository @Inject constructor(
    private val trackDao: TrackDao,
    private val placeDao: PlaceDao,
    private val routeDao: RouteDao,
    private val pointDao: PointDao,
    private val geoTrackerHelper: GeoTrackerHelper?
) : BaseRepository {

    override suspend fun fetchMaxSpeed(): PointDto? {
        val startTimestamp = currentYearTimestamp()
        return pointDao.fetchMaxSpeed(startTimestamp.timeInMillis)
            .firstOrNull()
    }

    override suspend fun fetchUnFinishedTracks(): List<TrackDto> {
        return trackDao.fetchUnFinishedTracks()
    }

    override suspend fun fetchAllTracks(): List<TrackDto> {
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

    override suspend fun fetchYearTracksStatistics(year: Int): List<TrackDto> {
        val startTimestamp = yearStartTimestamp(year)
        val endTimestamp = yearEndTimestamp(year)
        return trackDao.fetchAllTracks(startTimestamp.timeInMillis, endTimestamp.timeInMillis)
    }

    override suspend fun fetchYearTrackPoints(year: Int?, nthPointsToSkip: Int): Map<Long, List<SimplePointDto>> {
        val startTimestamp = year?.let { yearStartTimestamp(it) } ?: currentYearTimestamp()
        val endTimestamp = year?.let { yearEndTimestamp(it) } ?: currentYearEndTimestamp()
        return pointDao
            .fetchAllPoints(startTimestamp.timeInMillis, endTimestamp.timeInMillis, nthPointsToSkip)
            .groupBy { it.trackId }
    }

    override suspend fun fetchAllTrackPoints(nthPointsToSkip: Int): List<List<SimplePointDto>> {
        return pointDao
            .fetchAllPoints(nthPointsToSkip)
            .groupBy { it.trackId }
            .map { it.value }
    }

    override suspend fun fetchYears(): List<Int> {
        return trackDao.fetchDistinctYears()
    }

    override suspend fun add(pointDto: PointDto): Long {
        return pointDao.add(pointDto)
    }

    override suspend fun add(pointDtos: List<PointDto>) {
        pointDao.addAll(pointDtos)
    }

    override suspend fun add(trackDto: TrackDto): Long {
        return trackDao.add(trackDto)
    }

    override suspend fun add(placeDto: PlaceDto) {
        placeDao.add(placeDto)
    }

    override suspend fun update(trackId: Long, endTimestamp: Long) {
        trackDao.update(trackId, endTimestamp)
    }

    override suspend fun fetch(trackId: Long): TrackDto? {
        return trackDao.fetch(trackId)
    }

    override suspend fun deleteTrack(trackId: Long) {
        trackDao.delete(trackId)
    }

    override fun fetchLivePoints(): Flow<List<PointDto>> {
        return pointDao.fetchLivePoints()
    }

    override fun fetchLastPoint(): PointDto {
        return pointDao.fetchLastPoint()
    }

    override fun fetchLiveTrack(): Flow<TrackDto?> {
        return trackDao.fetchActiveTrack()
    }

    override suspend fun appendDistance(trackId: Long, distance: Float) {
        trackDao.appendDistance(trackId, distance)
    }

    override suspend fun updateTrack(trackId: Long, endTimestamp: Long, label: String?) {
        trackDao.update(trackId, label, endTimestamp)
    }

    override suspend fun saveLabel(trackId: Long, label: String?) {
        trackDao.update(trackId, label)
    }

    override suspend fun fetchTrackPoints(trackId: Long): List<PointDto> {
        return pointDao.fetchByTrack(trackId)
    }

    override suspend fun restoreLegacyDb() {
        trackDao.deleteAll()
        pointDao.deleteAll()
        routeDao.deleteAll()
        placeDao.deleteAll()
        prepareLegacyStorage()
    }

    override suspend fun deleteAllPoints(trackId: Long) {
        pointDao.deleteByTrack(trackId)
    }

    override suspend fun fetchPlacesToVisit(): List<PlaceDto> {
        return placeDao.fetchAllPlaces()
    }

    override suspend fun deletePlaceToVisit(id: Long) {
        placeDao.delete(id)
    }

    override suspend fun countPlacesToVisit(): Int {
        return placeDao.countAll()
    }

    override suspend fun countPoints(): Long {
        return pointDao.countPoints()
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

    private fun currentYearEndTimestamp(): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
    }

    private fun yearStartTimestamp(year: Int): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_YEAR, 1)
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
        trackDao.addAll((geoTrackerHelper?.getTracks() ?: emptyList()).map {
            TrackDto(it.id, it.label, it.startTimestamp, it.endTimestamp, it.distance)
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