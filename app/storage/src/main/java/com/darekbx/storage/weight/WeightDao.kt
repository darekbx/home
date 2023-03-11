package com.darekbx.storage.weight

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WeightDao {

    @Insert
    suspend fun addAll(weightDtos: List<WeightDto>)

    @Insert
    suspend fun add(weightDto: WeightDto)

    @Query("DELETE FROM weight_entry WHERE id = :entryId")
    suspend fun delete(entryId: Long)

    @Query("SELECT COUNT(id) FROM weight_entry")
    suspend fun countEntries(): Int

    @Query("SELECT * FROM weight_entry ORDER BY date ASC")
    suspend fun getEntries(): List<WeightDto>
}
