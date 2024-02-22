package com.darekbx.geotracker.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

sealed class StatisticsUiState {
    object Idle : StatisticsUiState()
    object InProgress : StatisticsUiState()
    data class Done(val any: Any) : StatisticsUiState()
}

@HiltViewModel
class StatisticsViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow<StatisticsUiState>(StatisticsUiState.Idle)
    val uiState: Flow<StatisticsUiState>
        get() = _uiState

    fun loadStatistics(year: Int) {
        viewModelScope.launch {
            _uiState.value = StatisticsUiState.InProgress



            _uiState.value = StatisticsUiState.Done(Unit)
        }
    }
}