package com.darekbx.geotracker.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darekbx.geotracker.ui.LoadingProgress

@Composable
fun SettingsScreen(settingsViewState: SettingsViewState = rememberSettingsViewState()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (settingsViewState.state) {
            SettingsUiState.Idle -> {
                Button(onClick = { settingsViewState.deleteAndRestore() }) {
                    Text(text = "Delete and restore")
                }
            }

            SettingsUiState.InProgress -> LoadingProgress()
        }
    }
}