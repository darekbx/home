package com.darekbx.geotracker.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.SimplePointDto

@Dao
interface PointDao {

    @Query("SELECT * FROM geo_point ORDER BY speed DESC LIMIT 1")
    suspend fun fetchMaxSpeed(): List<PointDto>

    @Insert
    suspend fun addAll(dtos: List<PointDto>)

    @Query("SELECT * FROM geo_point WHERE track_id = :trackId")
    fun fetchByTrack(trackId: Long): LiveData<List<PointDto>>

    @Query("SELECT * FROM geo_point WHERE track_id = :trackId AND ROWID % :nhtTwoToSkip == 0")
    fun fetchByTrackAsync(trackId: Long, nhtTwoToSkip: Int): List<PointDto>

    @Query("SELECT track_id, latitude, longitude FROM geo_point WHERE track_id = :trackId AND ROWID % :nhtTwoToSkip == 0")
    fun fetchSimpleByTrackAsync(trackId: Long, nhtTwoToSkip: Int): List<SimplePointDto>

    @Query("SELECT track_id, latitude, longitude FROM geo_point WHERE ROWID % :nhtTwoToSkip == 0")
    suspend fun fetchAllPoints(nhtTwoToSkip: Int): List<SimplePointDto>

    @Query("SELECT track_id, latitude, longitude FROM geo_point WHERE timestamp > :fromTimestamp AND ROWID % :nhtTwoToSkip == 0 ORDER BY timestamp DESC")
    suspend fun fetchAllPoints(fromTimestamp: Long, nhtTwoToSkip: Int): List<SimplePointDto>

    @Query("DELETE FROM geo_point WHERE track_id = :trackId")
    fun deleteByTrack(trackId: Long)

    @Query("DELETE FROM geo_point WHERE track_id = :trackId AND id >= :idFrom AND id <= :idTo")
    fun deletePoints(trackId: Long, idFrom: Long, idTo: Long)

    @Insert
    fun add(pointDto: PointDto)
}