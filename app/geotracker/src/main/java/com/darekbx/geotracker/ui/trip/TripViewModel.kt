package com.darekbx.geotracker.ui.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.domain.usecase.DeleteAllPointsUseCase
import com.darekbx.geotracker.domain.usecase.FixEndTimestampUseCase
import com.darekbx.geotracker.domain.usecase.GetAllTracksUseCase
import com.darekbx.geotracker.domain.usecase.GetTrackWithPoints
import com.darekbx.geotracker.domain.usecase.SaveLabelUseCase
import com.darekbx.geotracker.domain.usecase.TrackWithPointsWrapper
import com.darekbx.geotracker.domain.usecase.TrimPointsUseCase
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.repository.model.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TripUiState {
    object Idle : TripUiState()
    object InProgress : TripUiState()
    class Done(val data: TrackWithPointsWrapper, val allPoints: List<List<SimplePointDto>>) : TripUiState()
}

@HiltViewModel
class TripViewModel @Inject constructor(
    private val getTrackWithPoints: GetTrackWithPoints,
    private val deleteAllPointsUseCase: DeleteAllPointsUseCase,
    private val trimPointsUseCase: TrimPointsUseCase,
    private val saveLabelUseCase: SaveLabelUseCase,
    private val fixEndTimestampUseCase: FixEndTimestampUseCase,
    private val allTracksUseCase: GetAllTracksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripUiState>(TripUiState.Idle)
    val uiState: Flow<TripUiState>
        get() = _uiState

    fun fetch(trackId: Long) {
        viewModelScope.launch {
            _uiState.value = TripUiState.InProgress
            finishWithReload(trackId)
        }
    }

    fun deleteAllPoints(trackId: Long) {
        viewModelScope.launch {
            _uiState.value = TripUiState.InProgress
            deleteAllPointsUseCase(trackId)
            // Reload
            finishWithReload(trackId)
        }
    }

    fun trimPoints(trackId: Long, points: List<Point>) {
        viewModelScope.launch {
            _uiState.value = TripUiState.InProgress
            trimPointsUseCase(trackId, points)
            // Reload
            finishWithReload(trackId)
        }
    }

    fun saveLabel(trackId: Long, label: String?) {
        viewModelScope.launch {
            _uiState.value = TripUiState.InProgress
            saveLabelUseCase(trackId, label)
            // Reload
            finishWithReload(trackId)
        }
    }

    fun fixEndTimestamp(trackId: Long) {
        viewModelScope.launch {
            _uiState.value = TripUiState.InProgress
            fixEndTimestampUseCase(trackId)
            // Reload
            finishWithReload(trackId)
        }
    }

    private suspend fun finishWithReload(trackId: Long) {
        val data = getTrackWithPoints(trackId)
        val allPoints = allTracksUseCase(skipActual = false)
            .filter { it.first().trackId != trackId }
        _uiState.value = TripUiState.Done(data, allPoints)
    }
}