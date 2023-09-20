package com.darekbx.geotracker.ui.alltracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.domain.usecase.GetAllTracksUseCase
import com.darekbx.geotracker.repository.entities.SimplePointDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AllTracksUiState {
    object Idle : AllTracksUiState()
    object InProgress : AllTracksUiState()
    class Done(val data: List<List<SimplePointDto>>) : AllTracksUiState()
}

@HiltViewModel
class AllTracksViewModel @Inject constructor(
    private val getAllTracksUseCase: GetAllTracksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AllTracksUiState>(AllTracksUiState.Idle)
    val uiState: Flow<AllTracksUiState>
        get() = _uiState

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = AllTracksUiState.InProgress
        viewModelScope.launch {
            val latestTracks = getAllTracksUseCase(skipActual = false)
                _uiState.value = AllTracksUiState.Done(latestTracks)
        }
    }
}