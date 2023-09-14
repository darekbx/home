package com.darekbx.geotracker.ui.trips.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.domain.usecase.DeleteTrackUseCase
import com.darekbx.geotracker.domain.usecase.FetchTripsUseCase
import com.darekbx.geotracker.domain.usecase.FixEndTimestampUseCase
import com.darekbx.geotracker.domain.usecase.TripsWrapper
import com.darekbx.geotracker.repository.model.Track
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
    private val fetchTripsUseCase: FetchTripsUseCase,
    private val fixEndTimestampUseCase: FixEndTimestampUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripsUiState>(TripsUiState.Idle)
    val uiState: Flow<TripsUiState>
        get() = _uiState

    private var selectedYear: Int = 0

    fun fixEndTimestamp(trackId: Long) {
        viewModelScope.launch {
            fixEndTimestampUseCase.invoke(trackId)
        }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            _uiState.value = TripsUiState.InProgress
            deleteTrackUseCase(track.id)

            // Reload
            val trips = fetchTripsUseCase(selectedYear)
            _uiState.value = TripsUiState.Done(trips)
        }
    }

    fun loadTrips(year: Int) {
        viewModelScope.launch {
            selectedYear = year
            _uiState.value = TripsUiState.InProgress
            val trips = fetchTripsUseCase(year)
            _uiState.value = TripsUiState.Done(trips)
        }
    }
}
