package com.darekbx.geotracker.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BuildDrawCacheParams
import androidx.compose.ui.draw.DrawCacheModifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.InformationDialog
import com.darekbx.geotracker.repository.model.ActivityData
import com.darekbx.geotracker.repository.model.Summary
import com.darekbx.geotracker.repository.model.SummaryWrapper
import com.darekbx.geotracker.service.LocationService
import com.darekbx.geotracker.ui.defaultCard
import com.darekbx.geotracker.ui.home.activity.ActivityView
import com.darekbx.geotracker.ui.home.activity.Chart
import com.darekbx.geotracker.ui.home.activity.Header
import com.darekbx.geotracker.ui.home.mappreview.MapPreviewView
import com.darekbx.geotracker.ui.home.recording.RecordingScreen
import com.darekbx.geotracker.ui.home.recording.RecordingUiState
import com.darekbx.geotracker.ui.home.recording.RecordingViewState
import com.darekbx.geotracker.ui.home.recording.rememberRecordingViewState
import com.darekbx.geotracker.ui.home.summary.SummaryBox
import com.darekbx.geotracker.ui.home.summary.SummaryView
import com.darekbx.geotracker.ui.theme.GeoTrackerTheme
import com.darekbx.geotracker.ui.theme.LocalColors
import com.darekbx.geotracker.ui.theme.bounceClick
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    recordingViewState: RecordingViewState = rememberRecordingViewState(),
    openCalendar: () -> Unit = { }
) {
    val context = LocalContext.current
    val intent = Intent(context, LocationService::class.java)
    val isLocationEnabled by remember { homeScreenViewModel.isLocationEnabled() }
    var locationDisabledDialog by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var gpxUri by remember { mutableStateOf<Uri?>(null) }
    val state = recordingViewState.state

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
            result?.let { uri ->
                gpxUri = uri
                if (!LocationService.IS_RUNNING) {
                    context.startForegroundService(intent)
                    recordingViewState.setIsRecording()
                }
            }
        }

    LaunchedEffect(Unit) {
        recordingViewState.checkIsRecording()
    }

    LaunchedEffect(isLocationEnabled) {
        if (!isLocationEnabled) {
            locationDisabledDialog = true
        }
    }

    LaunchedEffect(state) {
        if (state is RecordingUiState.Stopped) {
            refreshKey++
        }
    }

    if (state !is RecordingUiState.Stopped) {
        RecordingScreen(gpxUri = gpxUri)
        // Don't show home screen while is recording
        return
    }

    val backgroundPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    )

    val foregroundPermissionState = rememberPermissionState(
        Manifest.permission.FOREGROUND_SERVICE,
    ) { granted ->
        if (granted) {
            backgroundPermissionState.launchPermissionRequest()
        }
    }

    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    ) { status ->
        if (status.all { it.value }) {
            foregroundPermissionState.launchPermissionRequest()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            key(refreshKey) {
                SummaryView()
                ActivityView { openCalendar() }
                MapPreviewView()
            }
        }

        Column(Modifier.padding(bottom = 16.dp)) {
            RecordButton(enabled = isLocationEnabled, gpx = true) {
                launcher.launch(arrayOf("*/*"))
            }
            RecordButton(enabled = isLocationEnabled, gpx = false) {
                when {
                    !locationPermissionState.allPermissionsGranted -> {
                        locationPermissionState.launchMultiplePermissionRequest()
                    }

                    !foregroundPermissionState.status.isGranted -> {
                        foregroundPermissionState.launchPermissionRequest()
                    }

                    !backgroundPermissionState.status.isGranted -> {
                        backgroundPermissionState.launchPermissionRequest()
                    }

                    else -> {
                        if (!LocationService.IS_RUNNING) {
                            // Permissions granted, start service!
                            context.startForegroundService(intent)
                            recordingViewState.setIsRecording()
                        }
                    }
                }
            }
        }
    }

    if (locationDisabledDialog) {
        InformationDialog("Please enable location!") {
            locationDisabledDialog = false
        }
    }
}

@Preview(showSystemUi = false, device = Devices.PIXEL_4_XL, backgroundColor = 0L)
@Composable
fun HomeScreenPreview() {
    val data = listOf(
        ActivityData(1, listOf(10000.0)),
        ActivityData(2, listOf(20000.0)),
        ActivityData(3, listOf(26000.0)),
        ActivityData(4, listOf(65000.0)),
        ActivityData(5, listOf(12000.0)),
        ActivityData(6, listOf(8000.0, 8000.0)),
        ActivityData(7, listOf(5000.0)),
        ActivityData(8, listOf(20000.0, 20000.0, 5000.0)),
        ActivityData(9, listOf(20000.0)),
        ActivityData(10, listOf(24000.0)),
        ActivityData(11, listOf(9000.0)),
        ActivityData(12, listOf(12000.0)),
        ActivityData(13, listOf(18000.0)),
        ActivityData(14, listOf(39000.0)),
        ActivityData(15, listOf(37000.0)),
        ActivityData(16, listOf(5000.0)),
    )

    GeoTrackerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .defaultCard()
                        .height(114.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SummaryBox(
                        summaryWrapper = SummaryWrapper(
                            Summary(5213.27, 8287800, 213),
                            Summary(2430.02, 21004, 81),
                            2411.0,
                        ),
                        maxSpeed = 52.4F
                    )
                }
                Box(
                    modifier = Modifier
                        .defaultCard()
                        .height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .then(object : DrawCacheModifier {
                                override fun onBuildCache(params: BuildDrawCacheParams) {
                                    params
                                }

                                override fun ContentDrawScope.draw() {
                                    drawContent()
                                }

                            }),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Header { }
                        Spacer(modifier = Modifier.height(12.dp))
                        Chart(
                            Modifier
                                .fillMaxSize(), data, true
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .defaultCard()
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun RecordButton(enabled: Boolean = true, gpx: Boolean = false, onClick: () -> Unit = { }) {
    val alpha by remember { derivedStateOf { if (enabled) 1F else 0.33F } }
    val modifier = if (enabled) {
        Modifier
            .bounceClick()
            .clickable { onClick() }
    } else {
        Modifier
    }

    Box(
        modifier
            .alpha(alpha)
            .padding(end = 24.dp, bottom = 16.dp)
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .padding(18.dp)
                .fillMaxSize()
                .border(2.dp, LocalColors.current.red, CircleShape)
                .padding(4.dp)
                .clip(CircleShape)
                .background(LocalColors.current.red)
        )
        if (gpx) {
            Text(
                text = "GPX",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
