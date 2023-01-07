package com.darekbx.weather.ui.weather

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.darekbx.weather.data.network.WeatherDataSource

@Composable
fun WeatherView(weatherViewModel: WeatherViewModel = hiltViewModel()) {
    val showMap = false
    val data by weatherViewModel.weatherConditions.collectAsState(initial = emptyMap())

    Box(
        modifier = Modifier
            .requiredSizeIn(maxWidth = 300.dp, maxHeight = 300.dp)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {

        if (data.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        } else {
            WeatherImage(data[WeatherDataSource.ImageType.STORM])
            WeatherImage(data[WeatherDataSource.ImageType.PROBABILITIES], alpha = 0.05F)
            WeatherImage(data[WeatherDataSource.ImageType.RAIN])

            if (showMap) {
                WeatherImage(data[WeatherDataSource.ImageType.MAP])
            }

            // To define/redefine points, open emulator, enable map and
            // click to log point location, then add/update points below
            DrawPoint(Offset(518.9475F, 315.917F)) // Wwa
            DrawPoint(Offset(618.94507F, 321.92285F)) // BP
            DrawPoint(Offset(470.95923F, 512.9385F)) // Zako
            DrawPoint(Offset(379.95947F, 105.93164F)) // Debki
        }
    }
}

@Composable
private fun WeatherImage(url: String?, alpha: Float = 1.0F) {
    Image(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    Log.v("Click position", "${it.x}F, ${it.y}F")
                }
            }
            .alpha(alpha),
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .build()
        ),
        contentDescription = ""
    )
}

@Composable
private fun DrawPoint(position: Offset) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(Color.White, radius = 5.0F, center = position)
        drawCircle(Color.Black, radius = 4.0F, center = position)
        drawCircle(Color.White, radius = 2.0F, center = position)
    }
}