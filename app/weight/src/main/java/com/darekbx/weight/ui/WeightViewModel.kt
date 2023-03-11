package com.darekbx.weight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.weight.data.WeightRepository
import com.darekbx.weight.data.model.EntryType
import com.darekbx.weight.data.model.WeightEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightViewModel @Inject constructor(
    private val weightRepository: WeightRepository
) : ViewModel() {

    fun getEntries() = flow {
        emit(weightRepository.getWeightEntries())
    }

    fun add(weight: Double, type: EntryType) {
        viewModelScope.launch {
            weightRepository.add(WeightEntry(null, System.currentTimeMillis(), weight, type))
        }
    }

    fun delete(entryId: Long?) {
        viewModelScope.launch {
            entryId?.let {
                weightRepository.delete(it)
            }
        }
    }
}
