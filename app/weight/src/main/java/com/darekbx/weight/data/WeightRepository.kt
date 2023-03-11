package com.darekbx.weight.data

import com.darekbx.storage.legacy.OwnSpaceHelper
import com.darekbx.storage.weight.WeightDao
import com.darekbx.storage.weight.WeightDto
import com.darekbx.weight.data.model.EntryType
import com.darekbx.weight.data.model.WeightEntry
import javax.inject.Inject

class WeightRepository @Inject constructor(
    private val weightDao: WeightDao,
    private val ownSpaceHelper: OwnSpaceHelper?
) {

    suspend fun delete(entryId: Long) {
        weightDao.delete(entryId)
    }

    suspend fun add(weightEntry: WeightEntry) {
        weightDao.add(
            with(weightEntry) {
                WeightDto(null, date, weight, type.type)
            }
        )
    }

    suspend fun getWeightEntries(): List<WeightEntry> {
        if (weightDao.countEntries() == 0) {
            fillFromLegacyDatabase()
        }

        return weightDao.getEntries()
            .map { dto ->
                val typeValue = EntryType.values().find { it.type == dto.type }
                    ?: throw IllegalStateException("Unknown type!")
                WeightEntry(dto.id!!, dto.date, dto.weight, typeValue)
            }
    }

    private suspend fun fillFromLegacyDatabase() {
        val listOfLegacyEntries = ownSpaceHelper?.getWeightEntries() ?: emptyList()
        weightDao.addAll(listOfLegacyEntries.map {
            WeightDto(null, it.date, it.weight, it.type)
        })
    }
}
