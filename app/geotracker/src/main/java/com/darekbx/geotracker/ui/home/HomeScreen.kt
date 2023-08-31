package com.darekbx.geotracker.ui.home

import android.Manifest
import android.content.Intent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
fun HomeScreen() {

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

        RecordButton {
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
                    // Permissions granted, start service!
                    val intent = Intent(context, LocationService::class.java)
                    context.startForegroundService(intent)
                }
            }
        }

    }
}

@Preview
@Composable
fun RecordButton(onClick: () -> Unit = { }) {
    Box(
        Modifier
            .bounceClick()
            .padding(24.dp)
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable { onClick() }
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
