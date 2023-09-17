package com.darekbx.geotracker.ui.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.domain.usecase.GetTrackWithPoints
import com.darekbx.geotracker.domain.usecase.TrackWithPointsWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TripUiState {
    object Idle : TripUiState()
    object InProgress : TripUiState()
    class Done(val data: TrackWithPointsWrapper) : TripUiState()
}

@HiltViewModel
class TripViewModel @Inject constructor(
    private val getTrackWithPoints: GetTrackWithPoints
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripUiState>(TripUiState.Idle)
    val uiState: Flow<TripUiState>
        get() = _uiState

    fun fetch(trackId: Long) {
        viewModelScope.launch {
            _uiState.value = TripUiState.InProgress
            val data = getTrackWithPoints.invoke(trackId)
            _uiState.value = TripUiState.Done(data)
        }
    }
}