package com.darekbx.fuel.data

import com.darekbx.fuel.model.FuelEntry
import com.darekbx.fuel.model.FuelType
import com.darekbx.storage.fuel.FuelDao
import com.darekbx.storage.fuel.FuelEntryDto
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class FuelRepository @Inject constructor(
    private val fuelDao: FuelDao
) {

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getEntries() = fuelDao.getEntries().map {
        it.map { dto ->
            val type = when (dto.type) {
                FuelType.ON.valueNum -> FuelType.ON
                else -> FuelType.PB95
            }
            FuelEntry(dto.id!!, inputFormat.format(dto.date.toLong()), dto.liters, dto.cost, type)
        }
    }

    suspend fun add(fuelEntryDto: FuelEntryDto) {
        fuelDao.add(fuelEntryDto)
    }

    suspend fun delete(entryId: Long) {
        fuelDao.delete(entryId)
    }

    suspend fun deleteAll() {
        fuelDao.deleteAll()
    }
}
