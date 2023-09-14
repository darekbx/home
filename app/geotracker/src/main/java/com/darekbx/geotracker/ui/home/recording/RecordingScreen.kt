package com.darekbx.geotracker.ui.home.recording

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.darekbx.geotracker.BuildConfig
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.service.LocationService
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.defaultCard
import com.darekbx.geotracker.ui.rememberMapWithLifecycle
import com.darekbx.geotracker.ui.theme.bounceClick
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@Composable
fun RecordingScreen(
    recordingViewState: RecordingViewState = rememberRecordingViewState()
) {
    val context = LocalContext.current
    val intent = Intent(context, LocationService::class.java)
    val allTracks by recordingViewState.fetchAllTracks().collectAsState(initial = emptyList())

    var isMapVisible by remember { mutableStateOf(false) }
    var map by remember { mutableStateOf<MapView?>(null) }
    val polyline by remember {
        mutableStateOf(Polyline().apply {
            outlinePaint.color = android.graphics.Color.parseColor("#3175A5")
            outlinePaint.strokeWidth = 10.0F
        })
    }

    LaunchedEffect(Unit) {
        recordingViewState.pointsFlow().collect { points ->
            if (points.isNotEmpty()) {
                isMapVisible = true
                val mapPoints = points.map { point -> GeoPoint(point.latitude, point.longitude) }
                polyline.setPoints(mapPoints)
                map?.run {
                    overlays[overlays.size - 1] = polyline
                    controller?.setCenter(mapPoints[0])
                    invalidate()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {

        when (recordingViewState.state) {
            RecordingUiState.Stopped -> {}
            RecordingUiState.Recording -> {
                Box(
                    modifier = Modifier
                        .defaultCard()
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isMapVisible) {
                        Column(Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.8F),
                                contentAlignment = Alignment.Center
                            ) {
                                PreviewMap(allTracks) { map = it }
                            }
                            Summary()
                        }
                    } else {
                        LoadingProgress()
                    }
                }
            }
        }

        StopButton {
            context.stopService(intent)
            recordingViewState.stopRecording()
        }
    }
}

@Preview
@Composable
fun StopButton(onClick: () -> Unit = { }) {
    Box(
        Modifier
            .bounceClick()
            .clickable { onClick() }
            .padding(24.dp)
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.White)

    ) {
        Box(
            Modifier
                .padding(20.dp)
                .fillMaxSize()
                .border(2.dp, Color.Red, RectangleShape)
                .clip(RectangleShape)
                .background(Color.Red)
        )
    }
}

@Composable
fun PreviewMap(historicalTracks: List<List<SimplePointDto>>, ready: (MapView) -> Unit) {
    val context = LocalContext.current
    val mapView = rememberMapWithLifecycle()
    val zoomToPlace = 17.0

    fun drawLine(
        collection: List<SimplePointDto>,
        map: MapView,
        color: Int
    ) {
        val polyline = Polyline().apply {
            outlinePaint.color = color
            outlinePaint.strokeWidth = 4.0F
        }

        val mapPoints = collection.map { point -> GeoPoint(point.latitude, point.longitude) }
        polyline.setPoints(mapPoints)
        map.overlays.add(polyline)
    }

    AndroidView(factory = { mapView }) { map ->
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osm", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME

        historicalTracks.forEach { collection ->
            drawLine(collection, map, android.graphics.Color.parseColor("#C4463B"))
        }

        map.controller.setZoom(zoomToPlace)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.overlays.add(Polyline())

        ready(map)
    }
}

@Composable
fun Summary() {

    Text(text = "Recording!")
}
