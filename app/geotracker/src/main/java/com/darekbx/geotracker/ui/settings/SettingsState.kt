package com.darekbx.geotracker.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel

class SettingsViewState(
    private val settingsViewModel: SettingsViewModel
) {
    val state: SettingsUiState
        @Composable get() = settingsViewModel.uiState.collectAsState(initial = SettingsUiState.Idle).value

    fun deleteAndRestore() {
        settingsViewModel.deleteAndRestore()
    }

    fun save(
        nthPointsToSkip: Int,
        gpsMinDistance: Float,
        gpsUpdateInterval: Long,
        showYearSummaryValue: Boolean,
        uploadLastLocation: Boolean,
    ) {
        settingsViewModel.save(
            nthPointsToSkip,
            gpsMinDistance,
            gpsUpdateInterval,
            showYearSummaryValue,
            uploadLastLocation
        )
    }

    fun synchronize(onProgress: (Int, Int) -> Unit) {
        settingsViewModel.synchronize(onProgress)
    }

    fun dataToSynchronize() = settingsViewModel.dataToSynchronize()
}

@Composable
fun rememberSettingsViewState(
    viewModel: SettingsViewModel = hiltViewModel()
) = remember {
    SettingsViewState(viewModel)
}