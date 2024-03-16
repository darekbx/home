package com.darekbx.fuel.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.fuel.model.FuelEntry
import com.darekbx.fuel.model.FuelType

@Composable
fun ChartScreen(chartViewModel: ChartViewModel = hiltViewModel()) {
    val entries by chartViewModel.entries.collectAsState(initial = emptyList())
    Canvas(modifier = Modifier.padding(top = 32.dp, bottom = 32.dp).fillMaxSize()) {
        ChartUtils.drawEntries(this, entries)
    }
}
@Composable
fun ChartScreen(modifier: Modifier, chartViewModel: ChartViewModel = hiltViewModel()) {
    val entries by chartViewModel.entries.collectAsState(initial = emptyList())
    Canvas(modifier = modifier.fillMaxWidth()) {
        ChartUtils.drawEntries(this, entries)
    }
}

object ChartUtils {

    private const val padding = 4F
    private val green = Color(0xFF4CAF50)
    private val black = Color.Black

    fun drawEntries(canvas: DrawScope, fuelEntries: List<FuelEntry>) {
        if (fuelEntries.isEmpty()) {
            return
        }

        val maxValue = fuelEntries.maxOf { it.pricePerLiter() }.toFloat()
        val minValue = fuelEntries.minOf { it.pricePerLiter() }.toFloat()
        val heightRatio = canvas.size.height / (maxValue - minValue)
        val widthStep = (canvas.size.width - padding) / (fuelEntries.size - 1)
        var start = 0F

        var color = Color.Black
        var lastType = FuelType.ON
        var p0 = Offset(
            start,
            convertPrice(canvas.size, fuelEntries[0].pricePerLiter(), minValue, heightRatio)
        )

        fuelEntries
            .drop(1)
            .forEach { entry ->
                val p1 = Offset(
                    start + widthStep,
                    convertPrice(canvas.size, entry.pricePerLiter(), minValue, heightRatio)
                )

                if (entry.type != lastType) {
                    color = if (entry.type == FuelType.ON) black else green
                }

                lastType = entry.type

                canvas.drawLine(color, p0, p1, strokeWidth = 2F)
                p0 = p1
                start += widthStep
            }

        val guidesCount = 6
        val guideHeightRatio = canvas.size.height / guidesCount

        (0..guidesCount).forEach { index ->
            val p0 = Offset(0F, guideHeightRatio * index)
            val p1 = Offset(canvas.size.width, guideHeightRatio * index)
            canvas.drawLine(Color.LightGray, p0, p1)
        }
    }

    private fun convertPrice(size: Size, price: Double, minValue: Float, heightRatio: Float): Float =
        size.height - ((price - minValue) * heightRatio).toFloat()
}
