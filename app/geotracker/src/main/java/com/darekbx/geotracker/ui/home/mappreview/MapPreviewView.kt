package com.darekbx.geotracker.ui.home.mappreview

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.darekbx.geotracker.BuildConfig
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.defaultCard
import com.darekbx.geotracker.ui.rememberMapWithLifecycle
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay

@Composable
fun MapPreviewView(
    modifier: Modifier = Modifier,
    mapPreviewViewState: MapPreviewViewState = rememberMapPreviewViewState()
) {
    val state = mapPreviewViewState.state

    LaunchedEffect(Unit) {
        mapPreviewViewState.refresh()
    }

    Box(
        modifier = modifier
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
            when (state) {
                is MapPreviewUiState.InProgress -> LoadingProgress()
                is MapPreviewUiState.Done -> MapView(state.data, mapPreviewViewState.mapPreferences)
                else -> {}
            }
        }
    }
}

@Composable
private fun MapView(data: Map<Long, List<SimplePointDto>>, mapPreferences: SharedPreferences) {
    val context = LocalContext.current
    val mapView = rememberMapWithLifecycle()
    val zoomToPlace = 13.0

    fun drawLine(
        collection: List<SimplePointDto>,
        map: MapView,
        color: Int
    ) {
        val polyline = Polyline().apply {
            outlinePaint.color = color
            outlinePaint.strokeWidth = 6.0F
        }

        val mapPoints = collection.map { point -> GeoPoint(point.latitude, point.longitude) }
        polyline.setPoints(mapPoints)
        map.overlays.add(polyline)
    }

    AndroidView(factory = { mapView }) { map ->
        Configuration.getInstance().load(context, mapPreferences)
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)

        // Draw tracks
        data.values.drop(1).forEach { collection ->
            drawLine(collection, map, Color.parseColor("#3BA732"))
        }
        drawLine(data.values.first(), map, Color.parseColor("#A73B32"))

        var lastLocation = GeoPoint(52.20, 21.02)
        data.values.firstOrNull()?.firstOrNull()?.let { lastPoint ->
            lastLocation = GeoPoint(lastPoint.latitude, lastPoint.longitude)
        }

        map.controller.apply {
            setZoom(zoomToPlace)
            setCenter(lastLocation)
        }
    }
}
