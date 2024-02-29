package com.darekbx.geotracker.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.domain.usecase.FetchStatisticsUseCase
import com.darekbx.geotracker.repository.model.YearSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StatisticsUiState {
    object Idle : StatisticsUiState()
    object InProgress : StatisticsUiState()
    data class Done(val yearSummaries: List<YearSummary>) : StatisticsUiState()
}

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val fetchStatisticsUseCase: FetchStatisticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatisticsUiState>(StatisticsUiState.Idle)
    val uiState: Flow<StatisticsUiState>
        get() = _uiState

    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = StatisticsUiState.InProgress
            val data = fetchStatisticsUseCase.invoke()
            _uiState.value = StatisticsUiState.Done(data)
        }
    }
}
