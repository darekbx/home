package com.darekbx.weather.ui.weather

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.darekbx.weather.BuildConfig
import com.darekbx.weather.data.network.ConditionsDataSource
import com.darekbx.weather.data.network.airly.Measurements

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel = hiltViewModel(),
    openSettings: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = openSettings,
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "settings")
                }
                Spacer(modifier = Modifier.padding(8.dp))
                FloatingActionButton(
                    onClick = weatherViewModel::updateState,
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "refresh")
                }
            }
        },
        content = { innerPadding ->
            WeatherView(
                modifier = Modifier.padding(innerPadding),
                weatherViewModel
            )
        }
    )
}

@Composable
fun WeatherView(
    modifier: Modifier,
    weatherViewModel: WeatherViewModel
) {    // To define/redefine points, open emulator, enable map and
    // click to log point location, then add/update points below
    val points = listOf(
        Offset(518.9475F, 315.917F), // Wwa
        Offset(618.94507F, 321.92285F), // BP
        Offset(470.95923F, 512.9385F), // Zako
        Offset(379.95947F, 105.93164F), // Debki
    )

    LaunchedEffect(Unit) {
        weatherViewModel.loadAirQuality()
    }

    Column(
        modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        /**
         * To manually recompose a view, let's define a state
         * On button click we are setting different state and
         * whole key() section content will be reloaded
         */
        val stateHolder by remember { weatherViewModel.stateHolder }
        key(stateHolder) {
            // Weather conditions
            val data by weatherViewModel.weatherConditions.collectAsState(initial = emptyMap())
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.5f)
                    .scale(1.3F),
                contentAlignment = Alignment.Center
            ) {
                WeatherBox(data, points)
            }
            // Air quality
            val measurements = weatherViewModel.measurementsList
            AirQualityView(measurements)
        }
    }
}

@Composable
private fun AirQualityView(measurements: List<Measurements>) {
    LazyColumn {
        items(measurements, key = { it.installationId }) { measurement ->
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 2.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = measurement.temperature,
                    modifier = Modifier.width(34.dp),
                    textAlign = TextAlign.Right,
                    color = Color.White,
                    fontSize = 10.sp
                )
                Text(
                    text = measurement.averagePMNorm,
                    color = Color(android.graphics.Color.parseColor(measurement.airlyIndex.color)),
                    modifier = Modifier.width(34.dp),
                    textAlign = TextAlign.Right,
                    fontSize = 10.sp
                )
                Text(
                    text = measurement.humidity,
                    modifier = Modifier.width(34.dp),
                    color = Color.White,
                    textAlign = TextAlign.Right,
                    fontSize = 10.sp
                )
                Text(
                    text = measurement.installation?.address?.toString() ?: "",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .fillMaxWidth(),
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }
    }

    measurements.lastOrNull()?.rateLimits?.run {
        Text(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 4.dp)
                .fillMaxWidth(),
            text = "Limits: $dayLimit/$dayRemaining, $minuteLimit/$minuteRemaining",
            color = Color.Gray,
            textAlign = TextAlign.Right,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun WeatherBox(data: Map<ConditionsDataSource.ImageType, String>, points: List<Offset>) {
    val showMap = false
    val size = 300.dp

    Box(
        modifier = Modifier
            .requiredSizeIn(maxWidth = size, maxHeight = size, minHeight = size, minWidth = size)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (data.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        } else {

            points.forEach {
                DrawPoint(position = it)
            }

            WeatherImage(data[ConditionsDataSource.ImageType.PROBABILITIES], alpha = 0.1F)
            WeatherImage(data[ConditionsDataSource.ImageType.STORM])
            WeatherImage(data[ConditionsDataSource.ImageType.RAIN])

            if (showMap) {
                WeatherImage(data[ConditionsDataSource.ImageType.MAP])
            }
        }
    }
}

@Composable
private fun WeatherImage(url: String?, alpha: Float = 1.0F) {
    Image(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                if (BuildConfig.DEBUG) {
                    detectTapGestures {
                        Log.v("Click position", "${it.x}F, ${it.y}F")
                    }
                }
            }
            .alpha(alpha),
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .build()
        ),
        contentDescription = "Weather image"
    )
}

@Composable
private fun DrawPoint(position: Offset) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(Color.White, radius = 3.5F, center = position)
        drawCircle(Color.Black, radius = 2.0F, center = position)
    }
}
