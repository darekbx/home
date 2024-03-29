package com.darekbx.riverstatus.waterlevel.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.riverstatus.commonui.ErrorBox
import com.darekbx.riverstatus.commonui.Progress
import com.darekbx.riverstatus.model.StationWrapper
import com.darekbx.riverstatus.model.WaterStateRecord
import com.darekbx.riverstatus.waterlevel.viewmodel.WaterLevelViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.abs

@Composable
fun WaterlevelScreen(
    waterLevelViewModel: WaterLevelViewModel = hiltViewModel(),
    stationId: Long
) {
    val state by waterLevelViewModel.state

    // Call this to import data
    /*val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        waterLevelViewModel.importFromAssets(context)
    }*/

    if (state.hasError) {
        ErrorBox(state.errorMessage!!)
    } else {
        var stationInfo by remember { mutableStateOf<StationWrapper?>(null) }

        LaunchedEffect(true) {
            withContext(Dispatchers.IO) {
                stationInfo = waterLevelViewModel.getStationInfo(stationId)
            }
        }

        stationInfo
            ?.let { WaterLevel(it) }
            ?: run { Progress() }
    }
}

@Composable
private fun WaterLevel(station: StationWrapper) {
    Column {
        StationDescription(station = station)
        WaterLevelChart(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            waterStateRecords = station.waterStateRecords.reversed()
        )
    }
}

@Composable
private fun WaterLevelChart(
    modifier: Modifier = Modifier,
    waterStateRecords: List<WaterStateRecord>
) {
    if (waterStateRecords.isEmpty()) {
        Text(text = "No data")
        return
    }
    Canvas(modifier = modifier, onDraw = {
        val leftOffset = 50.dp.toPx()
        val itemsToSkip = 1

        val width = size.width - leftOffset
        val chunkWidth = width / (waterStateRecords.size - itemsToSkip)
        val maximum = waterStateRecords.maxOf { it.value }
        val minimum = waterStateRecords.minOf { it.value }
        val latest = waterStateRecords.last().value
        val chunkHeightScale = size.height / (maximum - minimum)
        val latestDisplayTrashold = 5
        val showLatest = abs(latest - maximum) > latestDisplayTrashold ||
                abs(latest - minimum) > latestDisplayTrashold

        var previousLevel = waterStateRecords.first().value
        var x = 0F

        val paint = Paint().asFrameworkPaint().apply {
            color = android.graphics.Color.DKGRAY
            textSize = 24F
        }

        translate(left = leftOffset, top = 0f) {

            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "${maximum}cm",
                    8F - leftOffset,
                    8F,
                    paint
                )
                it.nativeCanvas.drawText(
                    "${minimum}cm",
                    8F - leftOffset,
                    (maximum - minimum) * chunkHeightScale + 8F,
                    paint
                )
                if (showLatest) {
                    it.nativeCanvas.drawText(
                        "${latest}cm",
                        8F - leftOffset,
                        (maximum - latest) * chunkHeightScale + 8F,
                        paint
                    )
                }
            }

            // Maximum line
            drawLine(
                Color.Gray,
                Offset(-leftOffset / 2F, 0F),
                Offset(size.width, 0F)
            )
            // Minimum line
            drawLine(
                Color.Gray,
                Offset(-leftOffset / 2F, (maximum - minimum) * chunkHeightScale),
                Offset(size.width, (maximum - minimum) * chunkHeightScale)
            )
            if (showLatest) {
                // Latest line
                drawLine(
                    Color.Gray,
                    Offset(-leftOffset / 2F, (maximum - latest) * chunkHeightScale),
                    Offset(size.width, (maximum - latest) * chunkHeightScale)
                )
            }

            for (record in waterStateRecords.drop(itemsToSkip)) {
                val firstPoint = Offset(x, (maximum - previousLevel) * chunkHeightScale)
                val secondPoint =
                    Offset(x + chunkWidth, (maximum - record.value) * chunkHeightScale)

                drawLine(Color(33, 107, 196), firstPoint, secondPoint, strokeWidth = 2F)

                x += chunkWidth
                previousLevel = record.value
            }
        }
    })
}

@Composable
fun StationDescription(modifier: Modifier = Modifier, station: StationWrapper) {
    val state = station.state.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }
    Column(modifier = modifier.padding(8.dp)) {
        Text(text = station.name, style = MaterialTheme.typography.titleMedium)
        Text(text = state, color = Color.DarkGray, style = MaterialTheme.typography.titleSmall)
        Text(text = "New rows: ${station.newRows}", color = Color.DarkGray, style = MaterialTheme.typography.titleSmall)
        Text(text = "Total rows: ${station.waterStateRecords.size}", color = Color.DarkGray, style = MaterialTheme.typography.titleSmall)
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun StationDescriptionPreview() {
    StationDescription(station = StationWrapper("WARSZAWA-BULWARY", "low", emptyList()))
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun ChartPreview() {
    val data = listOf(
        WaterStateRecord("", 34, ""),
        WaterStateRecord("", 34, ""),
        WaterStateRecord("", 32, ""),
        WaterStateRecord("", 32, ""),
        WaterStateRecord("", 31, ""),
        WaterStateRecord("", 31, ""),
        WaterStateRecord("", 31, ""),
        WaterStateRecord("", 30, ""),
        WaterStateRecord("", 30, ""),
        WaterStateRecord("", 30, ""),
        WaterStateRecord("", 30, ""),
        WaterStateRecord("", 34, ""),
        WaterStateRecord("", 35, ""),
        WaterStateRecord("", 35, ""),
        WaterStateRecord("", 34, ""),
        WaterStateRecord("", 34, ""),
        WaterStateRecord("", 34, ""),
        WaterStateRecord("", 36, ""),
        WaterStateRecord("", 37, ""),
        WaterStateRecord("", 37, ""),
        WaterStateRecord("", 37, ""),
        WaterStateRecord("", 34, ""),
        WaterStateRecord("", 32, ""),
        WaterStateRecord("", 28, ""),
        WaterStateRecord("", 29, ""),
        WaterStateRecord("", 34, ""),
        WaterStateRecord("", 36, ""),
        WaterStateRecord("", 38, ""),
        WaterStateRecord("", 40, ""),
        WaterStateRecord("", 44, ""),
        WaterStateRecord("", 47, ""),
        WaterStateRecord("", 50, ""),
        WaterStateRecord("", 54, ""),
        WaterStateRecord("", 58, "")
    )
    WaterLevelChart(
        Modifier
            .padding(8.dp)
            .width(300.dp)
            .height(200.dp), data)
}
