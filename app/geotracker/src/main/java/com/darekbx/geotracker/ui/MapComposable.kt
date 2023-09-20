package com.darekbx.geotracker.ui

import android.annotation.SuppressLint
import android.graphics.DashPathEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.repository.model.Point
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapBox(modifier: Modifier = Modifier, contents: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .defaultCard()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        contents()
    }
}

@SuppressLint("ResourceType")
@Composable
fun rememberMapWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = 100
        }
    }
    val lifecycleObserver = rememberMapLifecycleObserver(mapView = mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_DESTROY -> mapView.onDetach()
                else -> {}
            }
        }
    }

fun MapView.drawLine(
    collection: List<Point>,
    color: Int,
    width: Float = 6.0F
) {
    val polyline = Polyline().apply {
        outlinePaint.color = color
        outlinePaint.strokeWidth = width
    }

    val mapPoints = collection.map { point -> GeoPoint(point.latitude, point.longitude) }
    polyline.setPoints(mapPoints)
    overlays.add(polyline)
}

fun MapView.drawLine(
    collection: List<SimplePointDto>,
    dashed: Boolean = true
) {
    val polyline = Polyline().apply {
        outlinePaint.color = android.graphics.Color.parseColor("#C4463B")
        outlinePaint.strokeWidth = 6.0F
        if (dashed) {
            outlinePaint.pathEffect = DashPathEffect(floatArrayOf(20f, 10f, 20f), 0F)
        }
    }
    val mapPoints = collection.map { point -> GeoPoint(point.latitude, point.longitude) }
    polyline.setPoints(mapPoints)
    overlays.add(polyline)
}


fun Point.toGeoPoint() = GeoPoint(this.latitude, this.longitude)

fun SimplePointDto.toGeoPoint() = GeoPoint(this.latitude, this.longitude)