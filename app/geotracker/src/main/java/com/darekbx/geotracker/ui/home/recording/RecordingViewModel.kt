package com.darekbx.geotracker.ui.home.recording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.domain.usecase.GetAllTracksUseCase
import com.darekbx.geotracker.domain.usecase.GetRecordingStateUseCase
import com.darekbx.geotracker.domain.usecase.GetTrackPointsUseCase
import com.darekbx.geotracker.domain.usecase.StopRecordingUseCase
import com.darekbx.geotracker.service.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecordingUiState {
    object Stopped : RecordingUiState()
    object Recording : RecordingUiState()
}

@HiltViewModel
class RecordingViewModel @Inject constructor(
    private val getRecordingStateUseCase: GetRecordingStateUseCase,
    private val getTrackPointsUseCase: GetTrackPointsUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val getAllTracksUseCase: GetAllTracksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecordingUiState>(RecordingUiState.Stopped)
    val uiState: Flow<RecordingUiState>
        get() = _uiState

    fun fetchAllTracks() = flow {
        emit(getAllTracksUseCase.invoke())
    }

    fun listenForLocationUpdates() =
        getTrackPointsUseCase.invoke()

    fun stopRecording(label: String? = null) {
        viewModelScope.launch {
            getRecordingStateUseCase.invoke()?.let { activeTrack ->
                stopRecordingUseCase.invoke(activeTrack.id!!, label)
            }
        }
        _uiState.value = RecordingUiState.Stopped
    }

    fun setIsRecording() {
        _uiState.value = RecordingUiState.Recording
    }

    suspend fun checkIsRecording() {
        val track = getRecordingStateUseCase.invoke()
        if (track != null && LocationService.IS_RUNNING) {
            setIsRecording()
        }
    }
}
