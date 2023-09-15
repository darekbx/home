package com.darekbx.geotracker.ui.trip

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.geotracker.BuildConfig
import com.darekbx.geotracker.repository.model.Point
import com.darekbx.geotracker.ui.rememberMapWithLifecycle
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@Composable
fun TripScreen(trackId: Long,

               tripViewModel: TripViewModel = hiltViewModel()
               ) {

    val points by tripViewModel.data(trackId).collectAsState(initial = null)
    points?.let {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            PreviewMap(points = it.points)
        }
    }
}


@Composable
fun PreviewMap(points: List<Point>) {
    val context = LocalContext.current
    val mapView = rememberMapWithLifecycle()
    val zoomToPlace = 17.0

    fun drawLine(
        collection: List<Point>,
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

        drawLine(points, map, Color.parseColor("#C4463B"))

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.setZoom(zoomToPlace)
        map.controller.setCenter(points.first().toGeoPoint())
        map.overlays.add(Polyline())
    }
}

private fun Point.toGeoPoint() = GeoPoint(this.latitude, this.longitude)