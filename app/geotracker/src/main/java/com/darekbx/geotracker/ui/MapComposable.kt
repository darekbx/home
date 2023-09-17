package com.darekbx.geotracker.ui

import android.annotation.SuppressLint
import android.graphics.DashPathEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.repository.model.Point
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

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
    color: Int
) {
    val polyline = Polyline().apply {
        outlinePaint.color = color
        outlinePaint.strokeWidth = 4.0F
    }

    val mapPoints = collection.map { point -> GeoPoint(point.latitude, point.longitude) }
    polyline.setPoints(mapPoints)
    overlays.add(polyline)
}

fun MapView.drawDashedLine(
    collection: List<SimplePointDto>
) {
    val polyline = Polyline().apply {
        outlinePaint.color = android.graphics.Color.parseColor("#C4463B")
        outlinePaint.strokeWidth = 4.0F
        outlinePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 15f, 10f), 0F)
    }
    val mapPoints = collection.map { point -> GeoPoint(point.latitude, point.longitude) }
    polyline.setPoints(mapPoints)
    overlays.add(polyline)
}


fun Point.toGeoPoint() = GeoPoint(this.latitude, this.longitude)