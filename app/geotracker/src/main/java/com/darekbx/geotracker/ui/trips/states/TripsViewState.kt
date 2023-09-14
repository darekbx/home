package com.darekbx.geotracker.ui.trips.states

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.geotracker.repository.model.Track
import com.darekbx.geotracker.ui.trips.viewmodels.TripsUiState
import com.darekbx.geotracker.ui.trips.viewmodels.TripsViewModel

class TripsViewState(
    private val tripsViewModel: TripsViewModel
)  {
    val state: TripsUiState
        @Composable get() = tripsViewModel.uiState.collectAsState(initial = TripsUiState.Idle).value

    fun loadTrips(year: Int) {
        tripsViewModel.loadTrips(year)
    }

    fun delete(track: Track) {
        tripsViewModel.deleteTrack(track)
    }
}

@Composable
fun rememberTripsViewState(
    viewModel: TripsViewModel = hiltViewModel()
) = remember {
    TripsViewState(viewModel)
}