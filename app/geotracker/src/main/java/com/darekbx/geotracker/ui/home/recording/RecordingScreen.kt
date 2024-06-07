package com.darekbx.geotracker.ui.home.recording

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Picture
import android.net.Uri
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.geotracker.BuildConfig
import com.darekbx.geotracker.R
import com.darekbx.geotracker.gpx.Gpx
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.repository.model.PlaceToVisit
import com.darekbx.geotracker.repository.model.Point
import com.darekbx.geotracker.service.LocationService
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.defaultCard
import com.darekbx.geotracker.ui.drawLine
import com.darekbx.geotracker.ui.drawPoint
import com.darekbx.geotracker.ui.rememberMapWithLifecycle
import com.darekbx.geotracker.ui.theme.bounceClick
import kotlinx.coroutines.flow.collectLatest
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun RecordingScreen(
    gpxUri: Uri? = null,
    recordingViewState: RecordingViewState = rememberRecordingViewState(),
    recordingViewModel: RecordingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val intent = Intent(context, LocationService::class.java)
    val allTracks by recordingViewState.fetchAllTracks().collectAsState(initial = emptyList())
    val placesToVisit by recordingViewState.placesToVisit().collectAsState(initial = emptyList())
    val gpxTrack by recordingViewState.loadGpx(gpxUri).collectAsState(initial = null)

    var isMapVisible by remember { mutableStateOf(false) }
    var map by remember { mutableStateOf<MapView?>(null) }
    val polyline by remember {
        mutableStateOf(Polyline().apply {
            outlinePaint.color = android.graphics.Color.parseColor("#3175A5")
            outlinePaint.strokeWidth = 10.0F
        })
    }
    var positionMarker by remember {
        mutableStateOf<Marker?>(null)
    }

    var latestPoint by remember { mutableStateOf<Point?>(null) }

    LaunchedEffect(Unit) {
        recordingViewModel.lastPoint.collectLatest { lastPoint ->
            lastPoint?.let {
                  map?.controller?.setCenter(GeoPoint(it.latitude, it.longitude))
            }
        }
    }
    LaunchedEffect(Unit) {
        recordingViewModel.listenForLocationUpdates().collect { points ->
            if (points.isNotEmpty()) {
                isMapVisible = true

                val mapPoints = points.map { point -> GeoPoint(point.latitude, point.longitude) }
                polyline.setPoints(mapPoints)

                latestPoint = points.first()

                map?.run {
                    positionMarker?.position = GeoPoint(mapPoints.first())
                    overlays[overlays.size - 1] = polyline
                    if (!recordingViewModel.reCenterButtonVisible.value) {
                        controller?.setCenter(mapPoints[0])
                    }
                    invalidate()
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 8.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier.defaultCard().fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (recordingViewState.state) {
                RecordingUiState.Stopping -> LoadingProgress()
                RecordingUiState.Stopped -> {}
                RecordingUiState.Recording -> {
                    if (isMapVisible) {
                        Column(Modifier.fillMaxSize()) {
                            MapBox(Modifier
                                .weight(1F)
                                .drawWithCache {
                                    val mapWidth = this.size.width.toInt()
                                    val mapHeight = this.size.height.toInt()
                                    onDrawWithContent {
                                        val picture = Picture()
                                        val pictureCanvas = Canvas(picture.beginRecording(mapWidth, mapHeight))
                                        draw(this, this.layoutDirection, pictureCanvas, this.size) {
                                            this@onDrawWithContent.drawContent()
                                        }
                                        picture.endRecording()
                                        drawIntoCanvas { canvas ->
                                            canvas.nativeCanvas.drawPicture(picture)
                                            recordingViewModel.sendFrame(mapWidth, mapHeight, picture)
                                        }
                                    }
                                }) {
                                PreviewMap(
                                    allTracks, placesToVisit, gpxTrack,
                                    onPan = recordingViewModel::onPan
                                ) { mapView, marker ->
                                    map = mapView
                                    positionMarker = marker
                                }

                                RecenterButton(Modifier.align(Alignment.BottomStart))
                            }
                            RecordingSummary(Modifier)
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

@Composable
private fun RecenterButton(modifier: Modifier, recordingViewModel: RecordingViewModel = hiltViewModel()) {
    if (recordingViewModel.reCenterButtonVisible.value) {
        Button(
            modifier = modifier.padding(start = 12.dp, bottom = 12.dp),
            onClick = { recordingViewModel.onReCenter() }
        ) {
            Text(text = "Re-Center", color = Color.White)
        }
    }
}

@Composable
fun MapBox(modifier: Modifier, contents: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        contents()
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

@SuppressLint("ClickableViewAccessibility")
@Composable
fun PreviewMap(
    historicalTracks: List<List<SimplePointDto>>,
    placesToVisit: List<PlaceToVisit>,
    gpxTrack: Gpx?,
    onPan: () -> Unit,
    ready: (MapView, Marker) -> Unit
) {
    val context = LocalContext.current
    val mapView = rememberMapWithLifecycle()
    val zoomToPlace = 17.0

    AndroidView(factory = { mapView }) { map ->
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osm", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME

        map.overlays.removeIf { it is Marker }

        historicalTracks.forEach { collection ->
            map.drawLine(collection)
        }

        gpxTrack?.let {
            map.drawLine(it.points, android.graphics.Color.parseColor("#0A247D"), 8F)
        }

        placesToVisit.forEach {
            map.drawPoint(Point(it.latitude, it.longitude))
        }

        val positionMarker = Marker(map).apply {
            icon = context.getDrawable(R.drawable.ic_marker)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        }

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                onPan()
            }
            false
        }
        map.controller.setZoom(zoomToPlace)
        map.overlays.add(positionMarker)
        map.overlays.add(Polyline())

        ready(map, positionMarker)
    }
}
