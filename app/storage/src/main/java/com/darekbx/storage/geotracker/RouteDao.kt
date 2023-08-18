package com.darekbx.geotracker.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.geotracker.repository.entities.RouteDto
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    @Insert
    suspend fun addAll(dtos: List<RouteDto>)

    @Query("SELECT COUNT(id) FROM geo_route")
    fun countAll(): LiveData<Int>

    @Query("SELECT * FROM geo_route")
    fun fetchAllRoutes(): Flow<List<RouteDto>>

    @Query("DELETE FROM geo_route WHERE id = :routeId")
    fun delete(routeId: Long)

    @Insert
    fun add(routeDto: RouteDto)
}
