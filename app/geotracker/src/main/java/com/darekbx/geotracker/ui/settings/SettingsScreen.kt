package com.darekbx.geotracker.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.geotracker.navigation.BackPressHandler
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.theme.GeoTrackerTheme
import com.darekbx.geotracker.ui.theme.LocalStyles
import com.darekbx.geotracker.ui.theme.inputColors

@Composable
fun SettingsScreen(
    settingsViewState: SettingsViewState = rememberSettingsViewState(),
    navigateUp: () -> Unit
) {
    val state = settingsViewState.state
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp)
    ) {
        when (state) {
            SettingsUiState.Idle -> {}
            SettingsUiState.InProgress -> LoadingProgress()
            is SettingsUiState.Done -> {
                val (nthPointsToSkip, gpsMinDistance, gpsUpdateInterval) = state
                SettingsContainer(
                    nthPointsToSkip,
                    gpsMinDistance,
                    gpsUpdateInterval
                ) { nthPointsToSkipValue, gpsMinDistanceValue, gpsUpdateIntervalValue ->
                    settingsViewState.save(
                        nthPointsToSkipValue,
                        gpsMinDistanceValue,
                        gpsUpdateIntervalValue
                    )
                    navigateUp()
                }
            }
        }
    }
}

@Composable
fun SettingsContainer(
    nthPointsToSkip: Int,
    gpsMinDistance: Float,
    gpsUpdateInterval: Long,
    onSave: (Int, Float, Long) -> Unit
) {
    var nthPointsToSkipValue by remember { mutableIntStateOf(nthPointsToSkip) }
    var gpsMinDistanceValue by remember { mutableFloatStateOf(gpsMinDistance) }
    var gpsUpdateIntervalValue by remember { mutableLongStateOf(gpsUpdateInterval) }

    BackPressHandler {
        // Save settings on back
        onSave(nthPointsToSkipValue, gpsMinDistanceValue, gpsUpdateIntervalValue)
    }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        InputLabel("Nth points to skip")
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            value = "$nthPointsToSkipValue",
            onValueChange = {
                nthPointsToSkipValue = it.toIntOrNull() ?: 0
            },
            colors = inputColors(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        InputLabel("GPS min distance [m]")
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            value = "$gpsMinDistanceValue",
            onValueChange = { gpsMinDistanceValue = it.toFloatOrNull() ?: 0F },
            colors = inputColors(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        InputLabel("GPS update interval [s]")
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            value = "$gpsUpdateIntervalValue",
            onValueChange = { gpsUpdateIntervalValue = it.toLongOrNull() ?: 0L },
            colors = inputColors(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

    }
}

@Composable
private fun InputLabel(label: String) {
    Text(
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        text = label,
        style = LocalStyles.current.grayLabel,
        fontSize = 14.sp,
    )
}

@Preview
@Composable
private fun SettingsContainerPreview() {
    GeoTrackerTheme {
        SettingsContainer(
            nthPointsToSkip = 2,
            gpsMinDistance = 20F,
            gpsUpdateInterval = 50L
        ) { _, _, _ -> }
    }
}