package com.darekbx.geotracker.ui.placestovisit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.domain.usecase.AddPlaceToVisitUseCase
import com.darekbx.geotracker.domain.usecase.DeleteAllPointsUseCase
import com.darekbx.geotracker.domain.usecase.DeletePlaceToVisitUseCase
import com.darekbx.geotracker.domain.usecase.GetPlacesToVisitUseCase
import com.darekbx.geotracker.repository.model.PlaceToVisit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

sealed class PlacesToVisitUiState {
    object Idle : PlacesToVisitUiState()
    object InProgress : PlacesToVisitUiState()
    data class Done(val places: List<PlaceToVisit>) : PlacesToVisitUiState()
}

@HiltViewModel
class PlacesToVisitViewModel @Inject constructor(
    private val getPlacesToVisitUseCase: GetPlacesToVisitUseCase,
    private val deletePlaceToVisitUseCase: DeletePlaceToVisitUseCase,
    private val addPlaceToVisitUseCase: AddPlaceToVisitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlacesToVisitUiState>(PlacesToVisitUiState.Idle)
    val uiState: Flow<PlacesToVisitUiState>
        get() = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = PlacesToVisitUiState.InProgress
            val places = getPlacesToVisitUseCase()
            _uiState.value = PlacesToVisitUiState.Done(places)
        }
    }

    fun add(label: String?, point: GeoPoint?) {
        viewModelScope.launch {
            _uiState.value = PlacesToVisitUiState.InProgress
            if (label != null && point != null) {
                addPlaceToVisitUseCase(label, point)
            }

            // Reload
            refresh()
        }
    }

    fun delete(placeToVisit: PlaceToVisit) {
        viewModelScope.launch {
            _uiState.value = PlacesToVisitUiState.InProgress
            deletePlaceToVisitUseCase(placeToVisit.id)

            // Reload
            refresh()
        }
    }
}