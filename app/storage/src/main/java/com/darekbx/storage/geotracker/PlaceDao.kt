package com.darekbx.geotracker.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.geotracker.repository.entities.PlaceDto

@Dao
interface PlaceDao {

    @Insert
    suspend fun addAll(dtos: List<PlaceDto>)

    @Query("SELECT COUNT(id) FROM geo_place")
    fun countAll(): LiveData<Int>

    @Query("SELECT * FROM geo_place")
    fun fetchAllPlaces(): LiveData<List<PlaceDto>>

    @Query("DELETE FROM geo_place WHERE id = :placeId")
    fun delete(placeId: Long)

    @Insert
    fun add(placeDto: PlaceDto)

    @Query("DELETE FROM geo_place")
    suspend fun deleteAll()
}
