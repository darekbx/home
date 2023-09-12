package com.darekbx.geotracker.ui.trips.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.ui.trips.FetchTripsUseCase
import com.darekbx.geotracker.ui.trips.TripsWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TripsUiState {
    object Idle : TripsUiState()
    object InProgress : TripsUiState()
    class Done(val data: TripsWrapper) : TripsUiState()
}

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val fetchTripsUseCase: FetchTripsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripsUiState>(TripsUiState.Idle)
    val uiState: Flow<TripsUiState>
        get() = _uiState

    fun loadTrips(year: Int) {
        viewModelScope.launch {
            _uiState.value = TripsUiState.InProgress
            val trips = fetchTripsUseCase(year)
            _uiState.value = TripsUiState.Done(trips)
        }
    }
}
