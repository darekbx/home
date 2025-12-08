package com.darekbx.geotracker.ui.alltracks

import android.content.SharedPreferences
import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import com.darekbx.geotracker.ui.trips.YearsScroller
import com.darekbx.geotracker.ui.trips.states.YearsViewState
import com.darekbx.geotracker.ui.trips.states.rememberYearsViewState
import com.darekbx.geotracker.ui.trips.viewmodels.YearsUiState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView

@Composable
fun AllTracksScreen(
    viewState: AllTracksViewState = rememberAllTracksViewState(),
    yearsViewState: YearsViewState = rememberYearsViewState()
) {
    val state = viewState.state
    var year by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) {
        yearsViewState.loadYears()
    }

    MapBox {
        when (state) {
            is AllTracksUiState.Done -> PreviewMap(
                yearPoints = state.data.filter {
                    if (year == -1) {
                        true
                    } else {
                        it.key == year
                    }
                },
                placesToVisit = state.placesToVisit,
                mapPreferences = viewState.mapPreferences
            )

            AllTracksUiState.Idle -> {}
            AllTracksUiState.InProgress -> LoadingProgress()
        }

        yearsViewState.state.let {
            when (it) {
                is YearsUiState.Done -> YearsScroller(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopStart),
                    years = it.years,
                    currentYear = year,
                    withAll = true
                ) { selectedYear ->
                    year = selectedYear
                }

                YearsUiState.InProgress ->
                    LoadingProgress(
                        Modifier
                            .padding(4.dp)
                            .size(32.dp)
                    )

                YearsUiState.Idle -> {}
            }
        }
    }
}

@Composable
fun PreviewMap(
    yearPoints: Map<Int, List<List<SimplePointDto>>>,
    placesToVisit: List<PlaceToVisit>,
    mapPreferences: SharedPreferences
) {
    val context = LocalContext.current
    val mapView = rememberMapWithLifecycle()
    val zoomToPlace = 11.0

    AndroidView(factory = { mapView }) { map ->
        Configuration.getInstance().load(context, mapPreferences)
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(zoomToPlace)
        map.controller.setCenter(DEFAULT_LOCATION)
        map.overlays.removeAll { true }

        map.drawDistanceCircles()

        var alpha = 1F
        yearPoints.toSortedMap(compareByDescending { it }).forEach { _, tracks ->
            val color = Color.argb(alpha, 1F, 0F, 0F)
            tracks.forEach { points ->
                map.drawLine(points, dashed = false, color = color)
            }
            alpha -= 0.1F
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
