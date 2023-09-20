package com.darekbx.geotracker.ui.trip

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.darekbx.common.ui.ConfirmationDialog
import com.darekbx.geotracker.BuildConfig
import com.darekbx.geotracker.R
import com.darekbx.geotracker.repository.entities.SimplePointDto
import com.darekbx.geotracker.repository.model.Point
import com.darekbx.geotracker.repository.model.Track
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.defaultCard
import com.darekbx.geotracker.ui.drawLine
import com.darekbx.geotracker.ui.rememberMapWithLifecycle
import com.darekbx.geotracker.ui.theme.GeoTrackerTheme
import com.darekbx.geotracker.ui.theme.LocalStyles
import com.darekbx.geotracker.ui.toGeoPoint
import com.darekbx.geotracker.utils.DateTimeUtils
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import java.util.concurrent.TimeUnit

@Composable
fun TripScreen(
    trackId: Long,
    tripState: TripViewState = rememberTripViewState()
) {
    val state = tripState.state

    var confirmDelete by remember { mutableStateOf(false) }
    var labelDialog by remember { mutableStateOf(false) }
    var trimmedPoints by remember { mutableStateOf(emptyList<Point>()) }

    LaunchedEffect(trackId) {
        tripState.fetch(trackId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is TripUiState.Done -> {
                trimmedPoints = emptyList()

                var map by remember { mutableStateOf<MapView?>(null) }
                val polyline by remember {
                    mutableStateOf(Polyline().apply {
                        outlinePaint.color = android.graphics.Color.parseColor("#3175A5")
                        outlinePaint.strokeWidth = 10.0F
                    })
                }

                Column(Modifier.fillMaxSize()) {
                    SummaryBox(state.data.track) { labelDialog = true }
                    if (state.data.points.isNotEmpty()) {
                        SpeedChart(points = state.data.points)
                        AltitudeChart(points = state.data.points)
                        MapBox {
                            PreviewMap(
                                points = state.data.points,
                                allPoints = state.allPoints
                            ) { mapView ->
                                map = mapView
                            }
                        }
                        PointsControl(state.data.points.size) { start, end ->
                            val points = state.data.points.subList(start, end)
                            trimmedPoints = points
                            polyline.setPoints(points.map { it.toGeoPoint() })
                            map?.run {
                                overlays[overlays.size - 1] = polyline
                                invalidate()
                            }
                        }
                    }
                }
            }

            TripUiState.Idle -> {}
            TripUiState.InProgress -> LoadingProgress()
        }

        if (state is TripUiState.Done && state.data.points.isNotEmpty()) {
            ActionButtons(
                onDeleteClick = { confirmDelete = true },
                onSaveClick = { tripState.trimPoints(trackId, trimmedPoints) },
                onFixClick = { tripState.fixEndTimestamp(trackId) },
                showFixEndTimestamp = state.data.track.endTimestamp == null
            )
        }
    }

    if (confirmDelete) {
        ConfirmationDialog(
            "Delete all points?",
            "Delete",
            onDismiss = { confirmDelete = false },
            onConfirm = { tripState.deleteAllPoints(trackId) })
    }

    if (labelDialog) {
        if (state is TripUiState.Done)
            LabelDialog(
                label = state.data.track.label,
                title = "Trip label",
                onSave = {
                    labelDialog = false
                    tripState.saveLabel(trackId, it)
                },
                onDismiss = { labelDialog = false })
    }
}

@Composable
private fun ActionButtons(
    onDeleteClick: () -> Unit,
    onSaveClick: () -> Unit,
    onFixClick: () -> Unit,
    showFixEndTimestamp: Boolean
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Column(
            modifier = Modifier.padding(bottom = 128.dp, end = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showFixEndTimestamp) {
                RotateButton(painterResource(id = R.drawable.ic_fix)) {
                    onFixClick()
                }
            }
            RotateButton(painterResource(id = R.drawable.ic_save)) {
                onSaveClick()
            }
            RotateButton(painterResource(id = R.drawable.ic_delete)) {
                onDeleteClick()
            }
        }
    }
}

@Composable
private fun RotateButton(icon: Painter, onClick: () -> Unit) {
    var currentRotation by remember { mutableFloatStateOf(0f) }
    val rotation = remember { Animatable(currentRotation) }
    var clicked by remember { mutableIntStateOf(0) }

    LaunchedEffect(clicked) {
        if (clicked > 0) {
            rotation.animateTo(
                targetValue = currentRotation + 360f,
                animationSpec = repeatable(
                    animation = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
                    iterations = 1
                )
            ) {
                currentRotation = value
            }
        }
    }

    FloatingActionButton(
        onClick = {
            clicked++
            onClick()
        },
        shape = RoundedCornerShape(50)
    ) {
        Icon(
            modifier = Modifier.rotate(currentRotation),
            painter = icon,
            contentDescription = "button"
        )
    }
}

@Composable
fun PointsControl(pointsCount: Int, onRangeChanged: (Int, Int) -> Unit) {
    var sliderPosition by remember { mutableStateOf(0f..pointsCount.toFloat()) }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .defaultCard()
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        RangeSlider(
            value = sliderPosition,
            onValueChange = { range ->
                sliderPosition = range
                onRangeChanged(range.start.toInt(), range.endInclusive.toInt())
            },
            valueRange = 0F..(pointsCount.toFloat()),
            steps = pointsCount
        )
        Text(
            text = "${(sliderPosition.endInclusive - sliderPosition.start).toInt()} points, from ${sliderPosition.start.toInt()} to ${sliderPosition.endInclusive.toInt()} ",
            style = LocalStyles.current.grayLabel,
            fontSize = 14.sp,
        )
    }
}

@Composable
fun SpeedChart(points: List<Point>) {
    Column(
        Modifier
            .fillMaxWidth()
            .defaultCard()
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            text = "Speed chart",
            style = LocalStyles.current.grayLabel,
            fontSize = 14.sp,
        )
    }
}

@Composable
fun AltitudeChart(points: List<Point>) {
    Column(
        Modifier
            .fillMaxWidth()
            .defaultCard()
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            text = "Altitude chart",
            style = LocalStyles.current.grayLabel,
            fontSize = 14.sp,
        )
    }
}

@Composable
fun SummaryBox(track: Track, onEditClick: () -> Unit = { }) {
    Column(
        Modifier
            .fillMaxWidth()
            .defaultCard()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 2.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            val fullStartDate = DateTimeUtils.formattedDate(track.startTimestamp)
            val fullEndDate = DateTimeUtils.formattedDate(track.endTimestamp ?: 0L)
            Text(
                text = fullStartDate.split(" ")[0],
                style = LocalStyles.current.grayLabel,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = " from ",
                style = LocalStyles.current.grayLabel,
                fontSize = 14.sp,
            )
            Text(
                text = fullStartDate.split(" ")[1],
                style = LocalStyles.current.grayLabel,
                fontSize = 16.sp,
                color = Color.White
            )
            if (track.endTimestamp != null) {
                Text(
                    text = " to ",
                    style = LocalStyles.current.grayLabel,
                    fontSize = 14.sp,
                )
                Text(
                    text = fullEndDate.split(" ")[1],
                    style = LocalStyles.current.grayLabel,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = " (${track.timespanWithSeconds()}) ",
                    style = LocalStyles.current.grayLabel,
                    fontSize = 14.sp,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.padding(bottom = 3.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "%.2f".format((track.distance ?: 0F) / 1000.0),
                    style = LocalStyles.current.grayLabel,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "km (${track.pointsCount} points)",
                    style = LocalStyles.current.grayLabel,
                    fontSize = 18.sp,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.widthIn(max = 150.dp),
                    text = track.label ?: "[no label]",
                    style = LocalStyles.current.grayLabel,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "edit",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ColumnScope.MapBox(contents: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .defaultCard()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .weight(1F),
        contentAlignment = Alignment.Center
    ) {
        contents()
    }
}

@Composable
fun PreviewMap(allPoints: List<List<SimplePointDto>>, points: List<Point>, onMapReady: (MapView) -> Unit) {
    val context = LocalContext.current
    val mapView = rememberMapWithLifecycle()
    val zoomToPlace = 17.0

    AndroidView(factory = { mapView }) { map ->
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osm", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(zoomToPlace)
        map.controller.setCenter(points.first().toGeoPoint())

        allPoints.forEach {
            map.drawLine(it)
        }
        map.drawLine(points, android.graphics.Color.parseColor("#3175A5"), width = 8F)

        onMapReady(map)
    }
}

@Preview(widthDp = 410)
@Composable
fun SummaryBoxPreview() {
    GeoTrackerTheme {
        SummaryBox(
            Track(
                1L,
                "Label test test test test",
                System.currentTimeMillis(),
                System.currentTimeMillis()
                        + TimeUnit.HOURS.toMillis(2)
                        + TimeUnit.MINUTES.toMillis(13)
                        + TimeUnit.SECONDS.toMillis(25),
                5232F,
                12
            )
        )
    }
}
