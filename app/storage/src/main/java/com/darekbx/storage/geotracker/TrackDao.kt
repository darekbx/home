package com.darekbx.geotracker.repository

import androidx.room.*
import com.darekbx.geotracker.repository.entities.TrackDto
import com.darekbx.storage.geotracker.TrackPointsDto

@Dao
interface TrackDao {

    @Insert
    suspend fun addAll(dtos: List<TrackDto>)

    @Query("""
SELECT 
	geo_track.*,
	COUNT(geo_point.id) AS `pointsCount`
FROM geo_track 
LEFT OUTER JOIN geo_point ON geo_point.track_id = geo_track.id
GROUP BY geo_track.id
ORDER BY geo_track.id DESC
    """)
    fun fetchAll___2(): List<TrackPointsDto>

    @Query("SELECT * FROM geo_track")
    suspend fun fetchAll(): List<TrackDto>

    @Query("SELECT * FROM geo_track WHERE start_timestamp > :fromTimestamp")
    suspend fun fetchAll(fromTimestamp: Long): List<TrackDto>

    @Query("SELECT COUNT(id) FROM geo_track LIMIT 1")
    fun countAllTracks(): Int

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

    @Query("UPDATE geo_track SET distance = distance + :distance WHERE id = :trackId")
    fun appendDistance(trackId: Long, distance: Float)

    @Insert
    fun add(trackDto: TrackDto): Long

    @Query("DELETE FROM geo_track WHERE id = :trackId")
    fun delete(trackId: Long)

    @Query("UPDATE geo_track SET end_timestamp = :endTimestamp WHERE id = :trackId")
    fun updateDate(trackId: Long, endTimestamp: Long)
}
