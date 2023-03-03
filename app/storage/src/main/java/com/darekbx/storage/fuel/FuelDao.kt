package com.darekbx.storage.fuel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelDao {

    @Insert
    suspend fun add(fuelEntryDto: FuelEntryDto)

    @Query("DELETE FROM fuel_entry WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM fuel_entry")
    suspend fun deleteAll()

    @Query("SELECT * FROM fuel_entry ORDER BY `date` DESC")
    fun getEntries(): Flow<List<FuelEntryDto>>
}
