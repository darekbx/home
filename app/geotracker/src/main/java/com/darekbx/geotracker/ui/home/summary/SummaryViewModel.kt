package com.darekbx.geotracker.ui.home.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.repository.model.SummaryWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class SummaryUiState {
    object Idle : SummaryUiState()
    object InProgress : SummaryUiState()
    class Done(val data: SummaryWrapper, val maxSpeed: Float) : SummaryUiState()
}

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val getSummaryUseCase: GetSummaryUseCase,
    private val getMaxSpeedUseCase: GetMaxSpeedUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SummaryUiState>(SummaryUiState.Idle)
    val uiState: Flow<SummaryUiState>
        get() = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _uiState.value = SummaryUiState.InProgress
                val summary = getSummaryUseCase.getSummary()
                val maxSpeed = getMaxSpeedUseCase.getMaxSpeed()
                _uiState.value = SummaryUiState.Done(summary, maxSpeed)
            }
        }
    }
}
