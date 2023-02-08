package com.darekbx.lifetimememo.screens.memo.ui

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay

@Composable
fun LocationView(location: GeoPoint? = null, onClick: (Double, Double) -> Unit) {
    val mapView = rememberMapWithLifecycle()
    val defaultZoom = 8.0
    val zoomToPlace = 13.0
    val defaultLocation = GeoPoint(52.0, 21.0)
    Box(modifier = Modifier
        .padding(top = 16.dp)
        .fillMaxSize()) {
        AndroidView(factory = { mapView }) { mapView ->
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.controller.apply {
                setZoom(if (location != null) zoomToPlace else defaultZoom)
                setCenter(location ?: defaultLocation)
            }
            mapView.overlays.clear()
            mapView.overlays.add(object : Overlay() {
                override fun onSingleTapConfirmed(
                    e: MotionEvent,
                    map: MapView
                ): Boolean {
                    val proj = map.projection
                    val clickLocation = proj.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
                    onClick(clickLocation.latitude, clickLocation.longitude)
                    return super.onSingleTapConfirmed(e, mapView)
                }
            })

            location?.let {
                val marker = Marker(mapView).apply {
                    position = location
                    title = "$location"
                }
                mapView.overlays.add(marker)
            }
        }
    }
}

@SuppressLint("ResourceType")
@Composable
private fun rememberMapWithLifecycle(): MapView {
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
private fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember {
        LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_DESTROY -> mapView.onDetach()
                else -> { }
            }
        }
    }