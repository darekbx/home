package com.darekbx.geotracker.ui.home

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.InformationDialog
import com.darekbx.geotracker.service.LocationService
import com.darekbx.geotracker.ui.home.activity.ActivityView
import com.darekbx.geotracker.ui.home.mappreview.MapPreviewView
import com.darekbx.geotracker.ui.home.summary.SummaryView
import com.darekbx.geotracker.ui.theme.bounceClick
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {
    val isLocationEnabled by remember { homeScreenViewModel.isLocationEnabled() }
    var locationDisabledDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isLocationEnabled) {
        if (!isLocationEnabled) {
            locationDisabledDialog = true
        }
    }

    val context = LocalContext.current

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
            SummaryView()
            ActivityView()
            MapPreviewView()
        }

        RecordButton(enabled = isLocationEnabled) {
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
                    val intent = Intent(context, LocationService::class.java)
                    if (LocationService.IS_RUNNING) {
                        Log.v("darek", "stopService")
                        context.stopService(intent)
                    } else {
                        // Permissions granted, start service!
                        context.startForegroundService(intent)
                        Log.v("darek", "startForegroundService")
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

@Preview
@Composable
fun RecordButton(enabled: Boolean = true, onClick: () -> Unit = { }) {
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
            .padding(24.dp)
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.White)

    ) {
        Box(
            Modifier
                .padding(20.dp)
                .fillMaxSize()
                .border(2.dp, Color.Red, CircleShape)
                .padding(4.dp)
                .clip(CircleShape)
                .background(Color.Red)
        )
    }
}
