package com.darekbx.geotracker.ui.trip.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.geotracker.ui.theme.GeoTrackerTheme
import com.darekbx.geotracker.ui.theme.LocalColors
import com.darekbx.geotracker.utils.SpeedUtils

private const val TOP_PADDING = 10
private const val START_PADDING = 110F

@Composable
fun SpeedDataChart(
    modifier: Modifier = Modifier,
    tripId: Long,
    chartsViewModel: ChartsViewModel = hiltViewModel()
) {
    val data by chartsViewModel.fetchTripSpeed(tripId).collectAsState(initial = emptyList())
    if (data.isNotEmpty()) {
        ChartView(modifier = modifier, data = data.map { SpeedUtils.msToKm(it) }, unit = "km\\h")
    }
}

@Composable
fun AltitudeDataChart(
    modifier: Modifier = Modifier,
    tripId: Long,
    chartsViewModel: ChartsViewModel = hiltViewModel()
) {
    val data by chartsViewModel.fetchTripAltitude(tripId).collectAsState(initial = emptyList())
    if (data.isNotEmpty()) {
        ChartView(modifier = modifier, data = data, unit = "m")
    }
}

@Composable
fun ChartView(modifier: Modifier = Modifier, data: List<Float>, unit: String) {
    val textMeasurer = rememberTextMeasurer()
    val color = LocalColors.current.red
    Canvas(modifier = modifier) {
        drawEntries(this, color, data, unit, textMeasurer)
    }
}

fun drawEntries(
    canvas: DrawScope,
    color: Color,
    values: List<Float>,
    unit: String,
    textMeasurer: TextMeasurer
) {
    val height = canvas.size.height - TOP_PADDING
    val count = values.count()
    val maxValue = values.max()
    val minValue = values.min()
    val avgValue = values.average().toFloat()
    val widthRatio = (canvas.size.width - START_PADDING) / (count - 1).toFloat()
    val heightRatio = height / (maxValue - minValue)
    var p0 = Offset(START_PADDING, TOP_PADDING + height - ((values.first() - minValue) * heightRatio))
    val maxPosition = height - ((maxValue - minValue) * heightRatio)
    val avgPosition = height - ((avgValue - minValue) * heightRatio)

    var start = START_PADDING

    values
        .drop(1)
        .forEach { entry ->
            val p1 = Offset(start + widthRatio, TOP_PADDING + height - ((entry - minValue) * heightRatio))
            canvas.drawLine(color, p0, p1, strokeWidth = 3F)
            p0 = p1
            start += widthRatio
        }

    drawGuide(maxPosition, canvas, textMeasurer, maxValue, unit)
    drawGuide(avgPosition, canvas, textMeasurer, avgValue, unit)
}

private fun drawGuide(
    maxPosition: Float,
    canvas: DrawScope,
    textMeasurer: TextMeasurer,
    maxValue: Float,
    unit: String
) {
    val lineP0 = Offset(START_PADDING, TOP_PADDING + maxPosition)
    val lineP1 = Offset(canvas.size.width, TOP_PADDING + maxPosition)
    canvas.drawLine(Color.White, lineP0, lineP1, strokeWidth = 0.75F)
    canvas.drawText(
        textMeasurer = textMeasurer,
        text = "%.1f$unit".format(maxValue),
        topLeft = Offset(2F, lineP0.y - 6F),
        style = TextStyle(Color.White, fontSize = 8.sp),
    )
}

@Preview
@Composable
fun ChartPreview() {
    GeoTrackerTheme {
        ChartView(
            Modifier.size(300.dp, 50.dp), data = listOf(
                0.27000F,
                1.30999F,
                3.08999F,
                3.77999F,
                3.17000F,
                2.70000F,
                3.53999F,
                3.05999F,
                3.17000F,
                3.03999F,
                3.02999F,
                3.58999F,
                2.90000F,
                2.65000F,
                2.27999F,
                3.02999F,
                2.83999F,
                2.81999F,
                2.50999F,
                2.43000F,
                2.85999F,
                2.61999F,
                2.60999F,
                2.66000F,
                2.51999F,
                3.91000F,
                4.52999F,
                5.09000F,
                1.15000F,
                2.85999F,
                0.42000F
            ), unit = "km\\h"
        )
    }
}

@Preview
@Composable
fun ChartAltitudePreview() {
    GeoTrackerTheme {
        ChartView(
            Modifier.size(300.dp, 50.dp), data = listOf(
                123.50280F,
                123.51245F,
                123.62600F,
                123.70465F,
                125.29531F,
                124.87917F,
                125.52037F,
                125.10578F,
                124.80496F,
                126.63836F,
                127.42264F,
                127.41321F,
                124.97134F,
                125.18040F,
                125.22780F,
                125.07860F,
                125.29763F,
                127.43730F
            ), unit = "m"
        )
    }
}

