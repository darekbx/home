package com.darekbx.geotracker.ui.home.recording

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.domain.usecase.GetAllTracksUseCase
import com.darekbx.geotracker.domain.usecase.GetRecordingStateUseCase
import com.darekbx.geotracker.domain.usecase.GetActiveTrackPointsUseCase
import com.darekbx.geotracker.domain.usecase.GetActiveTrackUseCase
import com.darekbx.geotracker.domain.usecase.GetPlacesToVisitUseCase
import com.darekbx.geotracker.domain.usecase.StopRecordingUseCase
import com.darekbx.geotracker.gpx.GpxReader
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.service.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class RecordingUiState {
    object Stopped : RecordingUiState()
    object Stopping : RecordingUiState()
    object Recording : RecordingUiState()
}

@HiltViewModel
class RecordingViewModel @Inject constructor(
    private val getRecordingStateUseCase: GetRecordingStateUseCase,
    private val getActiveTrackPointsUseCase: GetActiveTrackPointsUseCase,
    private val getActiveTrackUseCase: GetActiveTrackUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val getAllTracksUseCase: GetAllTracksUseCase,
    private val getPlacesToVisitUseCase: GetPlacesToVisitUseCase,
    private val gpxReader: GpxReader,
    private val contentResolver: ContentResolver
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecordingUiState>(RecordingUiState.Stopped)
    val uiState: Flow<RecordingUiState>
        get() = _uiState

    var reCenterButtonVisible = mutableStateOf(false)

    var lastPoint = MutableStateFlow<PointDto?>(null)

    fun onPan() {
        reCenterButtonVisible.value = true
    }

    fun onReCenter() {
        reCenterButtonVisible.value = false

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                lastPoint.value = getActiveTrackPointsUseCase.getLastPoint()
            }
        }
    }

    fun fetchAllTracks() = flow {
        emit(getAllTracksUseCase.invoke())
    }

    fun placesToVisit() = flow {
        emit(getPlacesToVisitUseCase.invoke())
    }

    fun listenForLocationUpdates() =
        getActiveTrackPointsUseCase.invoke()

    fun listenForActiveTrack() =
        getActiveTrackUseCase.invoke()

    fun stopRecording(label: String? = null) {
        viewModelScope.launch {
            _uiState.value = RecordingUiState.Stopping
            delay(500L)
            getRecordingStateUseCase.invoke()?.let { activeTrack ->
                stopRecordingUseCase.invoke(activeTrack.id!!, label)
            }
            _uiState.value = RecordingUiState.Stopped
        }
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

    fun loadGpx(uri: Uri?) = flow {
        if (uri != null) {
            contentResolver.openInputStream(uri)?.use { stream ->
                try {
                    val gpx = gpxReader.readGpx(stream)
                    emit(gpx)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}
