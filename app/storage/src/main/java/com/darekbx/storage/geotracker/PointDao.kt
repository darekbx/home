package com.darekbx.geotracker.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.SimplePointDto
import kotlinx.coroutines.flow.Flow

@Dao
interface PointDao {

    @Query("SELECT * FROM geo_point ORDER BY speed DESC LIMIT 1")
    suspend fun fetchMaxSpeed(): List<PointDto>

    @Insert
    suspend fun addAll(dtos: List<PointDto>)

    @Insert
    suspend fun add(pointDto: PointDto): Long

    @Query("SELECT track_id, latitude, longitude FROM geo_point WHERE timestamp > :fromTimestamp AND ROWID % :nhtTwoToSkip == 0 ORDER BY timestamp DESC")
    suspend fun fetchAllPoints(fromTimestamp: Long, nhtTwoToSkip: Int): List<SimplePointDto>

    @Query("SELECT track_id, latitude, longitude FROM geo_point WHERE ROWID % :nhtTwoToSkip == 0 ORDER BY timestamp DESC")
    suspend fun fetchAllPoints(nhtTwoToSkip: Int): List<SimplePointDto>

    @Query("DELETE FROM geo_point WHERE track_id = :trackId")
    suspend fun deleteByTrack(trackId: Long)

    @Query("SELECT * FROM geo_point WHERE track_id = (SELECT id FROM geo_track WHERE end_timestamp IS NULL ORDER BY start_timestamp DESC LIMIT 1) ORDER BY timestamp DESC")
    fun fetchLivePoints(): Flow<List<PointDto>>

    @Query("SELECT * FROM geo_point WHERE track_id = :trackId")
    suspend fun fetchByTrack(trackId: Long): List<PointDto>


    /*
        @Query("SELECT * FROM geo_point WHERE track_id = :trackId")
        suspend fun fetchByTrack(trackId: Long): LiveData<List<PointDto>>

        @Query("SELECT * FROM geo_point WHERE track_id = :trackId AND ROWID % :nhtTwoToSkip == 0")
        suspend fun fetchByTrackAsync(trackId: Long, nhtTwoToSkip: Int): List<PointDto>

        @Query("SELECT track_id, latitude, longitude FROM geo_point WHERE track_id = :trackId AND ROWID % :nhtTwoToSkip == 0")
        suspend fun fetchSimpleByTrackAsync(trackId: Long, nhtTwoToSkip: Int): List<SimplePointDto>

        @Query("SELECT track_id, latitude, longitude FROM geo_point WHERE ROWID % :nhtTwoToSkip == 0")
        suspend fun fetchAllPoints(nhtTwoToSkip: Int): List<SimplePointDto>

        @Query("DELETE FROM geo_point WHERE track_id = :trackId")
        suspend fun deleteByTrack(trackId: Long)

        @Query("DELETE FROM geo_point WHERE track_id = :trackId AND id >= :idFrom AND id <= :idTo")
        suspend fun deletePoints(trackId: Long, idFrom: Long, idTo: Long)
    */
}
