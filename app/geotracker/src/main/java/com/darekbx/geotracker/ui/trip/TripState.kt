package com.darekbx.geotracker.ui.trip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel

class TripViewState(
    private val tripViewModel: TripViewModel
)  {
    val state: TripUiState
        @Composable get() = tripViewModel.uiState.collectAsState(initial = TripUiState.Idle).value

    fun fetch(tripId: Long) {
        tripViewModel.fetch(tripId)
    }
}

@Composable
fun rememberTripViewState(
    viewModel: TripViewModel = hiltViewModel()
) = remember {
    TripViewState(viewModel)
}