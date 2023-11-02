package com.darekbx.storage.timeline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TimelineDao {

    @Insert
    suspend fun add(categoryDto: TimelineCategoryDto)

    @Insert
    suspend fun add(timelineEntryDto: TimelineEntryDto)

    @Query("SELECT * FROM timeline_category")
    fun categoriesFlow(): Flow<List<TimelineCategoryDto>>

    @Query("SELECT * FROM timeline_entry ORDER BY timestamp")
    suspend fun getEntries(): List<TimelineEntryDto>

    @Query("SELECT * FROM timeline_entry WHERE category_id = :categoryId ORDER BY timestamp")
    suspend fun getEntries(categoryId: Long): List<TimelineEntryDto>

    @Query("DELETE FROM timeline_category WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: Long)

    @Query("DELETE FROM timeline_entry WHERE id = :entryId")
    suspend fun deleteEntry(entryId: Long)
}
