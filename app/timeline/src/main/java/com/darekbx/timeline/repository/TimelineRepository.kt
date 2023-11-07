package com.darekbx.timeline.repository

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
            .map { list ->
                list.map { dto ->
                    Category(dto.id!!, dto.name, dto.color)
                }
            }

    fun getEntriesFlow() =
        timelineDao.getEntriesFlow()
            .map { list ->
                list.map { dto ->
                    Entry(dto.id!!, dto.categoryId, dto.title, dto.description, dto.timestamp)
                }
            }

    suspend fun getEntries(categoryId: Long) =
        timelineDao.getEntries(categoryId)
}
