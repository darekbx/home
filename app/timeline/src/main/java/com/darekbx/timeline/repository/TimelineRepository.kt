package com.darekbx.timeline.repository

import android.util.Log
import com.darekbx.storage.timeline.TimelineCategoryDto
import com.darekbx.storage.timeline.TimelineDao
import com.darekbx.storage.timeline.TimelineEntryDto
import com.darekbx.timeline.model.Category
import com.darekbx.timeline.model.Entry
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TimelineRepository @Inject constructor(
    private val timelineDao: TimelineDao
) {

    suspend fun deleteAll() {
        timelineDao.deleteEntries()
    }

    suspend fun addCategory(name: String, color: Int) {
        timelineDao.add(TimelineCategoryDto(null, name, color))
    }

    suspend fun addEntry(categoryId: Long, title: String, description: String, timestamp: Long) {
        timelineDao.add(TimelineEntryDto(null, categoryId, title, description, timestamp))
    }

    suspend fun deleteCategory(categoryId: Long) {
        timelineDao.deleteCategory(categoryId)
    }

    suspend fun deleteEntry(entryId: Long) {
        timelineDao.deleteEntry(entryId)
    }

    fun categoriesFlow() =
        timelineDao.categoriesFlow()
            .map { list -> list.mapToDomain() }

    fun getEntriesFlow() =
        timelineDao.getEntriesFlow()
            .map { list ->
                val categories = timelineDao.categories().mapToDomain()
                list
                    .map { dto ->
                        Entry(
                            dto.id!!,
                            dto.categoryId,
                            dto.title,
                            dto.description,
                            dto.timestamp
                        ).apply {
                            category = categories.find { category-> this.categoryId == category.id }
                        }
                    }
            }

    suspend fun getEntries(categoryId: Long) =
        timelineDao.getEntries(categoryId)

    private fun List<TimelineCategoryDto>.mapToDomain() =
        map { dto ->
            Category(dto.id!!, dto.name, dto.color)
        }
}
