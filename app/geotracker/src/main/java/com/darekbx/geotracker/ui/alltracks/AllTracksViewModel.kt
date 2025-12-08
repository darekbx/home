package com.darekbx.geotracker.ui.alltracks

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.domain.usecase.GetAllTracksUseCase
import com.darekbx.geotracker.domain.usecase.GetPlacesToVisitUseCase
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.repository.model.PlaceToVisit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AllTracksUiState {
    object Idle : AllTracksUiState()
    object InProgress : AllTracksUiState()
    class Done(
        val data: Map<Int, List<List<SimplePointDto>>>,
        val placesToVisit: List<PlaceToVisit>
    ) : AllTracksUiState()
}

@HiltViewModel
class AllTracksViewModel @Inject constructor(
    private val getAllTracksUseCase: GetAllTracksUseCase,
    private val getPlacesToVisitUseCase: GetPlacesToVisitUseCase,
    private val osmMapPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<AllTracksUiState>(AllTracksUiState.Idle)
    val uiState: Flow<AllTracksUiState>
        get() = _uiState

    init {
        refresh()
    }

    val mapPreferences: SharedPreferences
        get() = osmMapPreferences

    fun refresh() {
        _uiState.value = AllTracksUiState.InProgress
        viewModelScope.launch {
            val placesToVisit = getPlacesToVisitUseCase()
            val latestTracks = getAllTracksUseCase(skipActual = false, yearMap = true)
            _uiState.value = AllTracksUiState.Done(latestTracks, placesToVisit)
        }
    }
}