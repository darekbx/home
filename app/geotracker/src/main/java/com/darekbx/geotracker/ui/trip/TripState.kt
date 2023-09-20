package com.darekbx.geotracker.ui.trip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.geotracker.repository.model.Point

class TripViewState(
    private val tripViewModel: TripViewModel
)  {
    val state: TripUiState
        @Composable get() = tripViewModel.uiState.collectAsState(initial = TripUiState.Idle).value

    fun fetch(tripId: Long) {
        tripViewModel.fetch(tripId)
    }

    fun deleteAllPoints(tripId: Long) {
        tripViewModel.deleteAllPoints(tripId)
    }

    fun trimPoints(tripId: Long, points: List<Point>) {
        tripViewModel.trimPoints(tripId, points)
    }

    fun saveLabel(tripId: Long, label: String?) {
        tripViewModel.saveLabel(tripId, label)
    }

    fun fixEndTimestamp(tripId: Long) {
        tripViewModel.fixEndTimestamp(tripId)
    }
}

@Composable
fun rememberTripViewState(
    viewModel: TripViewModel = hiltViewModel()
) = remember {
    TripViewState(viewModel)
}