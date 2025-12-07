package com.darekbx.geotracker.ui.home.mappreview

import android.content.SharedPreferences
import android.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.darekbx.geotracker.BuildConfig
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.defaultCard
import com.darekbx.geotracker.ui.rememberMapWithLifecycle
import com.darekbx.geotracker.ui.theme.LocalColors
import com.darekbx.geotracker.ui.trips.states.YearsViewState
import com.darekbx.geotracker.ui.trips.states.rememberYearsViewState
import com.darekbx.geotracker.ui.trips.viewmodels.YearsUiState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay

@Composable
fun MapPreviewView(
    modifier: Modifier = Modifier,
    mapPreviewViewState: MapPreviewViewState = rememberMapPreviewViewState(),
    yearsViewState: YearsViewState = rememberYearsViewState()
) {
    val state = mapPreviewViewState.state
    var year by remember { mutableIntStateOf(yearsViewState.currentYear()) }

    LaunchedEffect(year) {
        mapPreviewViewState.refresh(year)
    }

    LaunchedEffect(Unit) {
        yearsViewState.loadYears()
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
        yearsViewState.state.let {
            when (it) {
                is YearsUiState.Done -> YearsScroller(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth(),
                    years = it.years,
                    currentYear = year
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
private fun YearsScroller(
    modifier: Modifier = Modifier,
    years: List<Int>,
    currentYear: Int,
    onYearSelected: (Int) -> Unit = { }
) {
    Box(
        modifier = modifier.defaultCard(alpha = 0.7F),
        contentAlignment = Alignment.CenterStart
    ) {
        val state = rememberLazyListState()

        LaunchedEffect(years) {
            state.scrollToItem(years.size)
        }

        LazyRow(Modifier, state) {
            items(years) { year ->
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
                        .clickable { onYearSelected(year) },
                    text = "$year",
                    color = if (currentYear == year) LocalColors.current.red else androidx.compose.ui.graphics.Color.White,
                    fontSize = 16.sp,
                    fontWeight = if (currentYear == year) FontWeight.Bold else FontWeight.Normal,
                    style = TextStyle(
                        textDecoration = if (currentYear == year) TextDecoration.Underline else TextDecoration.None
                    )
                )
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
