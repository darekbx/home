package com.darekbx.geotracker.repository

import androidx.room.*
import com.darekbx.geotracker.repository.entities.TrackDto
import com.darekbx.storage.geotracker.TrackPointsDto
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert
    suspend fun addAll(dtos: List<TrackDto>)

    @Query("SELECT * FROM geo_track WHERE end_timestamp IS NULL ORDER BY start_timestamp DESC")
    suspend fun fetchUnFinishedTracks(): List<TrackDto>

    @Insert
    suspend fun add(trackDto: TrackDto): Long

    @Query("SELECT * FROM geo_track")
    suspend fun fetchAll(): List<TrackDto>

    @Query("SELECT * FROM geo_track WHERE start_timestamp > :fromTimestamp")
    suspend fun fetchAll(fromTimestamp: Long): List<TrackDto>

    @Query("SELECT COUNT(id) FROM geo_track LIMIT 1")
    suspend fun countAllTracks(): Int

    @Query("SELECT DISTINCT strftime('%Y', datetime(start_timestamp / 1000, 'unixepoch')) AS `year` FROM geo_track ORDER BY `year` ASC")
    suspend fun fetchDistinctYears(): List<Int>

    @Query("""
SELECT
    geo_track.*,
    COUNT(geo_point.id) AS `pointsCount`
FROM geo_track 
LEFT OUTER JOIN geo_point ON geo_point.track_id = geo_track.id
WHERE geo_track.start_timestamp > :fromTimestamp AND geo_track.start_timestamp < :toTimestamp
GROUP BY geo_track.id
ORDER BY geo_track.id DESC
    """)
    suspend fun fetchAll(fromTimestamp: Long, toTimestamp: Long): List<TrackPointsDto>

    @Query("UPDATE geo_track SET end_timestamp = :endTimestamp WHERE id = :trackId")
    suspend fun update(trackId: Long, endTimestamp: Long)

    @Query("SELECT * FROM geo_track WHERE id = :trackId")
    suspend fun fetch(trackId: Long): TrackDto?

    @Query("SELECT * FROM geo_track WHERE end_timestamp IS NULL LIMIT 1")
    fun fetchActiveTrack(): Flow<TrackDto?>

    @Query("DELETE FROM geo_track WHERE id = :trackId")
    suspend fun delete(trackId: Long)

    @Query("UPDATE geo_track SET distance = distance + :distance WHERE id = :trackId")
    suspend fun appendDistance(trackId: Long, distance: Float)

    @Query("UPDATE geo_track SET label = :label, end_timestamp = :endTimestamp WHERE id = :trackId")
    suspend fun update(trackId: Long, label: String?, endTimestamp: Long)

    @Query("UPDATE geo_track SET label = :label WHERE id = :trackId")
    suspend fun update(trackId: Long, label: String?)

    @Query("DELETE FROM geo_track")
    suspend fun deleteAll()

    /*
        @Deprecated("Use fetchAllPoints")
        @Query("SELECT * FROM geo_track ORDER BY id ASC")
        fun fetchAllAscending(): List<TrackDto>

        @Query("SELECT * FROM geo_track WHERE id = :trackId")
        fun fetch(trackId: Long): TrackDto?

        @Query("UPDATE geo_track SET label = :label, end_timestamp = :endTimestamp WHERE id = :trackId")
        fun update(trackId: Long, label: String?, endTimestamp: Long)

        @Query("UPDATE geo_track SET end_timestamp = :endTimestamp WHERE id = :trackId")
        fun update(trackId: Long, endTimestamp: Long)

        @Query("UPDATE geo_track SET distance = :distance WHERE id = :trackId")
        fun updateDistance(trackId: Long, distance: Float)

        @Query("DELETE FROM geo_track WHERE id = :trackId")
        fun delete(trackId: Long)

        @Query("UPDATE geo_track SET end_timestamp = :endTimestamp WHERE id = :trackId")
        fun updateDate(trackId: Long, endTimestamp: Long)
        */
}
