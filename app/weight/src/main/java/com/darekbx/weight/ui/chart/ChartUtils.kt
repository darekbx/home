@file:OptIn(ExperimentalTextApi::class)

package com.darekbx.weight.ui.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import com.darekbx.weight.data.model.WeightEntry

class ChartUtils(private val entries: List<WeightEntry>) {

    private val padding = 64F

    private val minWeight by lazy { entries.minOf { it.weight } }
    private val maxWeight by lazy { entries.maxOf { it.weight } }

    fun drawEntries(canvas: DrawScope, color: Color, entries: List<WeightEntry>) {
        val heightRatio = canvas.size.height / maxWeight
        val widthStep = canvas.size.width / entries.size
        var start = padding

        var p0 = Offset(
            start,
            convertWeight(canvas.size, entries[0].weight, heightRatio).toFloat()
        )

        entries
            .drop(1)
            .forEach { entry ->
                val p1 = Offset(
                    start + widthStep,
                    convertWeight(canvas.size, entry.weight, heightRatio).toFloat()
                )
                canvas.drawLine(color, p0, p1)
                p0 = p1
                start += widthStep
            }
    }

    fun drawGuideLine(
        canvas: DrawScope,
        textMeasurer: TextMeasurer,
        weight: Double
    ) {
        val heightRatio = canvas.size.height / maxWeight
        val y = convertWeight(canvas.size, weight, heightRatio).toFloat()
        val p0 = Offset(padding, y)
        val p1 = Offset(canvas.size.width, y)
        canvas.drawLine(Color(0x1F000000), p0, p1)
        canvas.drawText(
            textMeasurer = textMeasurer,
            text = "${weight.toInt()}",
            topLeft = Offset(2F, p0.y - 6F),
            style = TextStyle(Color.Gray)
        )
    }

    private fun convertWeight(size: Size, weight: Double, heightRatio: Double) =
        size.height.toDouble() / 1.2 - ((weight - minWeight) * heightRatio)
}