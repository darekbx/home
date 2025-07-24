package com.darekbx.geotracker.ui.alltracks

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.darekbx.geotracker.BuildConfig
import com.darekbx.geotracker.DEFAULT_LOCATION
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.repository.model.PlaceToVisit
import com.darekbx.geotracker.repository.model.Point
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.MapBox
import com.darekbx.geotracker.ui.drawCircle
import com.darekbx.geotracker.ui.drawLine
import com.darekbx.geotracker.ui.drawPoint
import com.darekbx.geotracker.ui.rememberMapWithLifecycle
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView

@Composable
fun AllTracksScreen(viewState: AllTracksViewState = rememberAllTracksViewState()) {
    val state = viewState.state
    MapBox {
        when (state) {
            is AllTracksUiState.Done -> PreviewMap(
                points = state.data,
                placesToVisit = state.placesToVisit,
                mapPreferences = viewState.mapPreferences
            )

            AllTracksUiState.Idle -> {}
            AllTracksUiState.InProgress -> LoadingProgress()
        }
    }
}

@Composable
fun PreviewMap(
    points: List<List<SimplePointDto>>,
    placesToVisit: List<PlaceToVisit>,
    mapPreferences: SharedPreferences
) {
    val context = LocalContext.current
    val mapView = rememberMapWithLifecycle()
    val zoomToPlace = 12.0

    AndroidView(factory = { mapView }) { map ->
        Configuration.getInstance().load(context, mapPreferences)
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(zoomToPlace)
        map.controller.setCenter(DEFAULT_LOCATION)

        map.drawDistanceCircles()

        points.forEach {
            map.drawLine(it, dashed = false)
        }

        placesToVisit.forEach {
            map.drawPoint(Point(it.latitude, it.longitude))
        }
    }
}

private fun MapView.drawDistanceCircles() {
    listOf(10_000.0, 20_000.0, 30_000.0)
        .forEach { radius -> drawCircle(DEFAULT_LOCATION, radius) }
}
