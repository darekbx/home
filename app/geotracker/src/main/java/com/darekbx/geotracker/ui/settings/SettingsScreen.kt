package com.darekbx.geotracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.theme.GeoTrackerTheme
import com.darekbx.geotracker.ui.theme.LocalStyles
import com.darekbx.geotracker.ui.theme.inputColors

@Composable
fun SettingsScreen(
    settingsViewState: SettingsViewState = rememberSettingsViewState()
) {
    val state = settingsViewState.state
    val dataToSynchronize by settingsViewState.dataToSynchronize().collectAsState(initial = null)
    var isSyncRunning by remember { mutableStateOf(false) }
    var addManuallyDialog by remember { mutableStateOf(false) }
    var syncProgress by remember { mutableStateOf(Pair(1000, 0)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp)
    ) {
        when (state) {
            SettingsUiState.Idle -> {}
            SettingsUiState.InProgress -> LoadingProgress()
            is SettingsUiState.Done -> {
                val (nthPointsToSkip, gpsMinDistance, gpsUpdateInterval, showYearSummary, uploadLastLocation) = state
                SettingsContainer(
                    nthPointsToSkip,
                    gpsMinDistance,
                    gpsUpdateInterval,
                    showYearSummary,
                    uploadLastLocation,
                    dataToSynchronize,
                    onSave = { nthPointsToSkipValue, gpsMinDistanceValue, gpsUpdateIntervalValue, showYearSummaryValue, uploadLastLocation ->
                        settingsViewState.save(
                            nthPointsToSkipValue,
                            gpsMinDistanceValue,
                            gpsUpdateIntervalValue,
                            showYearSummaryValue,
                            uploadLastLocation
                        )
                    },
                    onSynchronizeClick = {
                        isSyncRunning = true
                        settingsViewState.synchronize { progress, max ->
                            syncProgress = Pair(progress, max)
                            if (progress == max) {
                                isSyncRunning = false
                            }
                        }
                    },
                    onAddManuallyClick = {
                        addManuallyDialog = true
                    }
                )
            }
        }
    }

    if (isSyncRunning) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5F)),
            contentAlignment = Alignment.Center
        ) {
            SyncProgress(Modifier.size(128.dp), syncProgress)
        }
    }

    if (addManuallyDialog) {
        ManualTripDialog(
            onSave = { distance, start, end ->
                settingsViewState.addManually(distance, start, end)
            },
            onDismiss = { addManuallyDialog = false }
        )
    }
}

@Composable
fun SyncProgress(modifier: Modifier = Modifier, progress: Pair<Int, Int>) {
    CircularProgressIndicator(
        progress = { progress.first / progress.second.toFloat() },
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer, CircleShape
            )
            .padding(8.dp)
    )
}

@Composable
fun SettingsContainer(
    nthPointsToSkip: Int,
    gpsMinDistance: Float,
    gpsUpdateInterval: Long,
    showYearSummary: Boolean,
    uploadLastLocation: Boolean,
    dataToSynchronize: Int?,
    onSave: (Int, Float, Long, Boolean, Boolean) -> Unit,
    onSynchronizeClick: () -> Unit,
    onAddManuallyClick: () -> Unit
) {
    var nthPointsToSkipValue by remember { mutableIntStateOf(nthPointsToSkip) }
    var gpsMinDistanceValue by remember { mutableFloatStateOf(gpsMinDistance) }
    var gpsUpdateIntervalValue by remember { mutableLongStateOf(gpsUpdateInterval) }
    var showYearSummaryValue by remember { mutableStateOf(showYearSummary) }
    var uploadLastLocationValue by remember { mutableStateOf(uploadLastLocation) }

    fun save() {
        // Save settings
        onSave(
            nthPointsToSkipValue,
            gpsMinDistanceValue,
            gpsUpdateIntervalValue,
            showYearSummaryValue,
            uploadLastLocationValue
        )
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
                save()
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
            onValueChange = {
                gpsMinDistanceValue = it.toFloatOrNull() ?: 0F
                save()
            },
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
            onValueChange = {
                gpsUpdateIntervalValue = it.toLongOrNull() ?: 0L
                save()
            },
            colors = inputColors(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        InputLabel("Show full year in Daily distances")
        Checkbox(
            checked = showYearSummaryValue,
            onCheckedChange = {
                showYearSummaryValue = it
                save()
            }
        )
        InputLabel("Upload last location")
        Checkbox(
            checked = uploadLastLocationValue,
            onCheckedChange = {
                uploadLastLocationValue = it
                save()
            }
        )

        InputLabel("Synchronization with Firebase Cloud")
        Button(modifier = Modifier.fillMaxWidth(), onClick = onSynchronizeClick) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Synchronize",
                    color = Color.Black,
                    modifier = Modifier.padding(end = 4.dp)
                )
                if (dataToSynchronize == null) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .size(18.dp)
                    )
                } else {
                    Text(text = "($dataToSynchronize new tracks)", color = Color.Black)
                }
            }
        }

        Button(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), onClick = onAddManuallyClick) {
            Text(text = "Add trip manually", color = Color.Black)
        }
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
            gpsUpdateInterval = 50L,
            showYearSummary = true,
            uploadLastLocation = true,
            dataToSynchronize = null,
            { _, _, _, _, _ -> },
            { },
            { }
        )
    }
}