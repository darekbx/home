package com.darekbx.fuel.ui.list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.fuel.BuildConfig
import com.darekbx.fuel.data.FuelRepository
import com.darekbx.fuel.model.FuelType
import com.darekbx.storage.fuel.FuelEntryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FuelViewModel @Inject constructor(
    private val fuelRepository: FuelRepository
) : ViewModel() {

    val entries = fuelRepository.getEntries()

    fun add(liters: Double, price: Double, type: FuelType) {
        viewModelScope.launch {
            fuelRepository.add(
                FuelEntryDto(
                    null,
                    "${System.currentTimeMillis()}",
                    liters,
                    price,
                    type.valueNum
                )
            )
        }
    }

    fun delete(entryId: Long) {
        viewModelScope.launch {
            fuelRepository.delete(entryId)
        }
    }

    /**
     * Warning: calling this method will delete all data!
     */
    @Suppress("unused")
    fun importFromAssets(context: Context) {
        require(BuildConfig.DEBUG)

        viewModelScope.launch {
            val file = "fuel_dump_02_03_23.csv"
            context.assets.open(file).use {
                fuelRepository.deleteAll()
                it.bufferedReader()
                    .readLines()
                    .drop(1) // Skip CSV columns description
                    .forEach { line ->
                    val chunks = line.split(',')
                    val id = chunks[0].toLong()
                    val date = chunks[1]
                    val liters = chunks[2].toDouble()
                    val price = chunks[3].toDouble()
                    val type = chunks[4].toInt()
                    fuelRepository.add(FuelEntryDto(id, "${date}000", liters, price, type))
                }
            }
        }
    }
}
