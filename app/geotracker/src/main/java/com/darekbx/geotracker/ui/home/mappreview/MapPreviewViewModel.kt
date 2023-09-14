package com.darekbx.geotracker.ui.home.mappreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.domain.usecase.GetLatestTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MapPreviewUiState {
    object Idle : MapPreviewUiState()
    object InProgress : MapPreviewUiState()
    class Done(val data: Map<Long, List<SimplePointDto>>) : MapPreviewUiState()
}

@HiltViewModel
class MapPreviewViewModel @Inject constructor(
    private val getLatestTracksUseCase: GetLatestTracksUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapPreviewUiState>(MapPreviewUiState.Idle)
    val uiState: Flow<MapPreviewUiState>
        get() = _uiState

    init {
        _uiState.value = MapPreviewUiState.InProgress
        viewModelScope.launch {
            val latestTracks = getLatestTracksUseCase()
            if (latestTracks.isEmpty()) {
                _uiState.value = MapPreviewUiState.Idle
            } else {
                _uiState.value = MapPreviewUiState.Done(latestTracks)
            }
        }
    }
}