package com.darekbx.geotracker.ui.alltracks

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel

class AllTracksViewState(
    private val allTracksViewModel: AllTracksViewModel
) {
    val state: AllTracksUiState
        @Composable get() = allTracksViewModel.uiState.collectAsState(initial = AllTracksUiState.Idle).value

    val mapPreferences: SharedPreferences
        get() = allTracksViewModel.mapPreferences

    fun refresh() {
        allTracksViewModel.refresh()
    }
}

@Composable
fun rememberAllTracksViewState(
    allTracksViewModel: AllTracksViewModel = hiltViewModel(),
) = remember {
    AllTracksViewState(allTracksViewModel)
}