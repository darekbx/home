package com.darekbx.geotracker.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.geotracker.repository.entities.PlaceDto

@Dao
interface PlaceDao {

    @Insert
    suspend fun addAll(dtos: List<PlaceDto>)

    @Query("DELETE FROM geo_place")
    suspend fun deleteAll()

    @Query("SELECT * FROM geo_place")
    suspend fun fetchAllPlaces(): List<PlaceDto>

    @Query("DELETE FROM geo_place WHERE id = :placeId")
    suspend fun delete(placeId: Long)

    @Query("SELECT COUNT(id) FROM geo_place")
    suspend fun countAll(): Int

    @Insert
    suspend fun add(placeDto: PlaceDto)
}
