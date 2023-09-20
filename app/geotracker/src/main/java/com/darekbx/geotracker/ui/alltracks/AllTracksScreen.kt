package com.darekbx.geotracker.ui.alltracks

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.darekbx.geotracker.BuildConfig
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.MapBox
import com.darekbx.geotracker.ui.drawLine
import com.darekbx.geotracker.ui.rememberMapWithLifecycle
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

@Composable
fun AllTracksScreen(viewState: AllTracksViewState = rememberAllTracksViewState()) {
    val state = viewState.state
    MapBox {
        when (state) {
            is AllTracksUiState.Done -> PreviewMap(points = state.data)
            AllTracksUiState.Idle -> {}
            AllTracksUiState.InProgress -> LoadingProgress()
        }
    }
}

@Composable
fun PreviewMap(points: List<List<SimplePointDto>>) {
    val context = LocalContext.current
    val mapView = rememberMapWithLifecycle()
    val zoomToPlace = 12.0

    AndroidView(factory = { mapView }) { map ->
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osm", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(zoomToPlace)
        map.controller.setCenter(GeoPoint(52.180338, 21.021554)) // Metro Wilanowska

        points.forEach {
            map.drawLine(it, dashed = false)
        }
    }
}