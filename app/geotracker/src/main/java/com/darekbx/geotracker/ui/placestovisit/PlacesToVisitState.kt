package com.darekbx.geotracker.ui.placestovisit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.geotracker.repository.model.PlaceToVisit
import org.osmdroid.util.GeoPoint

class PlacesToVisitState(
    private val placesToVisitViewModel: PlacesToVisitViewModel
)  {
    val state: PlacesToVisitUiState
        @Composable get() = placesToVisitViewModel.uiState.collectAsState(initial = PlacesToVisitUiState.Idle).value

    fun delete(placeToVisit: PlaceToVisit) {
        placesToVisitViewModel.delete(placeToVisit)
    }

    fun add(label: String?, point: GeoPoint?) {
        placesToVisitViewModel.add(label, point)
    }
}

@Composable
fun rememberPlacesToVisitViewState(
    viewModel: PlacesToVisitViewModel = hiltViewModel()
) = remember {
    PlacesToVisitState(viewModel)
}