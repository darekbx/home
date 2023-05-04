package com.darekbx.dotpad.ui.statistics

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.dotpad.R
import com.darekbx.dotpad.model.StatisticValue
import com.darekbx.dotpad.ui.dots.toColor
import com.darekbx.dotpad.ui.dots.toIntColor
import com.darekbx.dotpad.ui.theme.dotBlue
import com.darekbx.dotpad.ui.theme.dotRed
import com.darekbx.dotpad.ui.theme.dotTeal
import com.darekbx.dotpad.ui.theme.dotYellow

@Composable
fun StatisticsScreen(
    allCount: State<Int>,
    colorStatistics: State<List<StatisticValue>>,
    sizeStatistics: State<List<StatisticValue>>
) {
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            text = stringResource(R.string.all_dots_label, allCount.value)
        )

        Text(
            modifier = Modifier
                .padding(top = 48.dp, bottom = 8.dp)
                .align(Alignment.CenterHorizontally),
            color = Color.White,
            text = stringResource(R.string.colors_label)
        )
        PieChart(
            Modifier
                .size(200.dp, 200.dp)
                .align(Alignment.CenterHorizontally),
            statistics = colorStatistics.value
        )

        Text(
            modifier = Modifier
                .padding(top = 48.dp, bottom = 8.dp)
                .align(Alignment.CenterHorizontally),
            color = Color.White,
            text = stringResource(R.string.sizes_label)
        )
        ColumnChart(
            Modifier
                .size(200.dp, 200.dp)
                .align(Alignment.CenterHorizontally),
            statistics = sizeStatistics.value
        )
    }
}

@Composable
private fun PieChart(modifier: Modifier = Modifier, statistics: List<StatisticValue>) {
    if (statistics.isEmpty()) return

    Canvas(modifier = modifier) {
        val padding = 4F
        val canvasWidth = size.width - padding * 2
        val canvasHeight = size.height - padding * 2
        val chartArea = calculateChartRectange(canvasWidth, canvasHeight)

        val offset = Offset(chartArea.left + padding, chartArea.top + padding)
        val size = Size(chartArea.width(), chartArea.height())

        var angleStart = 0F
        val angles = mutableListOf<Float>()

        statistics.forEach {
            val color = it.value
            val arcTo = it.percent * 3.6F

            drawArc(Color(color), angleStart, arcTo, true, offset, size)
            drawArc(Color.Black, angleStart, arcTo, true, offset, size, style = Stroke(4.0F))

            angleStart += arcTo
            angles.add(arcTo)
        }
    }
}

@Composable
fun ColumnChart(modifier: Modifier = Modifier, statistics: List<StatisticValue>) {
    if (statistics.isEmpty()) return

    val paint = Paint()
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 42f
    paint.color = Color.White.toArgb()

    Canvas(modifier = modifier) {
        val padding = 8F
        val bottomSpace = 48F
        val canvasWidth = size.width - padding * 2
        val canvasHeight = size.height - padding - bottomSpace

        val heightRatio = canvasHeight / statistics.maxOf { it.percent }
        val partSize = canvasWidth / statistics.size

        // Flip chart
        scale(1F, -1F) {
            statistics.forEachIndexed { index, statisticValue ->
                val offset = Offset(index * partSize + padding, padding + bottomSpace)
                val size = Size(partSize, statisticValue.percent * heightRatio - padding)
                drawRect(dotBlue.toColor(), offset, size)
                drawRect(Color.Black, offset, size, style = Stroke(padding / 2F))
            }
        }

        statistics.forEachIndexed { index, statisticValue ->
            val offset = Offset(
                index * partSize + padding + partSize / 2,
                canvasHeight + bottomSpace
            )
            drawIntoCanvas {
                it.nativeCanvas.drawText("${statisticValue.value}", offset.x, offset.y, paint)
            }
        }
    }
}

private fun calculateChartRectange(width: Float, height: Float): RectF {
    var xOffset = 0F
    var yOffset = 0F
    val size = when (width > height) {
        true -> {
            xOffset = (width - height) / 2F
            height
        }
        else -> {
            yOffset = (height - width) / 2F
            width
        }
    }
    return RectF(xOffset, yOffset, size + xOffset, size + yOffset)
}

@SuppressLint("ProduceStateDoesNotAssignValue")
@Preview
@Composable
fun ChartScreenPreview() {
    val colors = listOf(
        StatisticValue(45F, dotYellow.toIntColor()),
        StatisticValue(25F, dotRed.toIntColor()),
        StatisticValue(30F, dotTeal.toIntColor())
    )
    val sizes = listOf(
        StatisticValue(55F, 10),
        StatisticValue(5F, 8),
        StatisticValue(30F, 6),
        StatisticValue(10F, 5)
    )
    StatisticsScreen(
        produceState(initialValue = 1000, producer = { }),
        produceState(initialValue = colors, producer = { }),
        produceState(initialValue = sizes, producer = { })
    )
}

@Preview("Color chart")
@Composable
fun PieChartPreview() {
    val colors = listOf(
        StatisticValue(45F, dotYellow.toIntColor()),
        StatisticValue(25F, dotRed.toIntColor()),
        StatisticValue(30F, dotTeal.toIntColor())
    )
    PieChart(
        Modifier.size(200.dp, 200.dp),
        statistics = colors
    )
}

@Preview("Sizes chart")
@Composable
fun ColumnChartPreview() {
    val sizes = listOf(
        StatisticValue(55F, 10),
        StatisticValue(5F, 8),
        StatisticValue(30F, 6),
        StatisticValue(10F, 5)
    )
    ColumnChart(
        Modifier.size(200.dp, 200.dp),
        statistics = sizes
    )
}
