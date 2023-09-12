package com.darekbx.geotracker.ui.trips.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.ui.trips.FetchYearsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

sealed class YearsUiState {
    object Idle : YearsUiState()
    object InProgress : YearsUiState()
    class Done(val years: List<Int>) : YearsUiState()
}

@HiltViewModel
class YearsViewModel @Inject constructor(
    private val fetchYearsUseCase: FetchYearsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<YearsUiState>(YearsUiState.Idle)
    val uiState: Flow<YearsUiState>
        get() = _uiState

    fun loadYears() {
        viewModelScope.launch {
            _uiState.value = YearsUiState.InProgress
            val years = fetchYearsUseCase()
            _uiState.value = YearsUiState.Done(years)
        }
    }

    fun currentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }
}
