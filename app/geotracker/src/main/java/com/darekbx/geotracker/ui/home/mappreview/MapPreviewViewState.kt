package com.darekbx.geotracker.ui.home.mappreview

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel

class MapPreviewViewState(
    private val mapPreviewViewModel: MapPreviewViewModel
) {
    val state: MapPreviewUiState
        @Composable get() = mapPreviewViewModel.uiState
            .collectAsState(initial = MapPreviewUiState.Idle).value

    val mapPreferences: SharedPreferences
        get() = mapPreviewViewModel.mapPreferences

    fun refresh(year: Int) {
        mapPreviewViewModel.refresh(year)
    }
}

@Composable
fun rememberMapPreviewViewState(
    mapPreviewViewModel: MapPreviewViewModel = hiltViewModel()
) = remember {
    MapPreviewViewState(mapPreviewViewModel)
}