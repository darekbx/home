package com.darekbx.storage.riverstatus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WaterLevelDao {

    @Query("SELECT * FROM water_level ORDER BY date DESC")
    fun fetch(): List<WaterLevelDto>

    @Query("SELECT * FROM water_level ORDER BY date DESC LIMIT 1")
    fun fetchLast(): WaterLevelDto?

    @Query("DELETE FROM water_level")
    fun deleteAll()

    @Insert
    fun insert(warterLevelDtos: List<WaterLevelDto>)

    @Insert
    fun insert(warterLevelDto: WaterLevelDto)
}
