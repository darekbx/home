package com.darekbx.stocks.ui.stocks

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.darekbx.stocks.model.Status
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

@SuppressLint("ModifierParameter")
@Preview(name = "PLN/USD", device = Devices.PIXEL_2_XL, showSystemUi = true)
@Composable
fun CommonChart(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(150.dp),
    data: List<Double> = TestData.PLN_USD,
    label: String = "PLN/USD",
    chartColor: Long = Color.Red.value.toLong(),
    guideLinesStep: Float = 0.02f,
    status: Status = Status.PLUS,
    unit: String = "zł"
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    val count = data.count()
    val maxValue = (data.maxOrNull() ?: 1.0)
    val minValue = (data.minOrNull() ?: 1.0)
    val roundMin = (minValue * 100.0).roundToInt() / 100.0
    val roundMax = (maxValue * 100.0).roundToInt() / 100.0

    Column(modifier = Modifier.wrapContentSize(Alignment.TopCenter)) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 0.dp),
            textAlign = TextAlign.Center,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(label)
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                    append(" (${data.size} entries)")
                }
            },
            style = MaterialTheme.typography.titleSmall,
            color = Color.LightGray
        )

        Canvas(modifier = modifier
            .background(Color.Black.copy(alpha = 0.075F))
            .onGloballyPositioned { containerSize = it.size }
        ) {
            val guideLineStep = calculateGuideLineStep(roundMin, roundMax)
            val height = containerSize.height
            val width = containerSize.width
            val widthRatio = width / (count - 1).toFloat()
            val heightRatio = height / (maxValue - minValue)

            var firstPoint = Offset(
                0F,
                (height - ((data.first() - minValue) * heightRatio)).toFloat()
            )

            generateSequence(roundMin) { it + guideLineStep }
                .takeWhile { it < roundMax }
                .forEach { v ->
                    val y = height - ((v - minValue) * heightRatio).toFloat()
                    drawLine(Color.DarkGray, Offset(0F, y), Offset(width.toFloat(), y))
                }

            data.forEachIndexed { index, value ->
                if (index == 0) {
                    return@forEachIndexed
                }

                val x = (index * widthRatio)
                val y = height - ((value - minValue) * heightRatio).toFloat()

                drawLine(
                    Color(chartColor.toULong()),
                    Offset(firstPoint.x, firstPoint.y),
                    Offset(x, y),
                    strokeWidth = 2.5f
                )
                firstPoint = Offset(x, y)
            }
        }

        DrawLegend(minValue, maxValue, data, status, unit)
    }
}

@Composable
private fun DrawLegend(
    minValue: Double,
    maxValue: Double,
    data: List<Double>,
    status: Status,
    unit: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val color = when (status) {
            Status.EQUAL -> Color.White
            Status.PLUS -> Color(56, 142, 60)
            Status.MINUS -> Color(229, 115, 115)
        }

        Text(
            text = "Actual: %.2f$unit".format(data.last()),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = "Min: %.2f$unit".format(minValue),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
        Text(
            text = "Max: %.2f$unit".format(maxValue),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
    }
}

private fun calculateGuideLineStep(minValue: Double, maxValue: Double, targetLineCount: Int = 15): Float {
    val range = maxValue - minValue

    // If the range is too small, use a minimum step
    if (range < 0.01) return 0.001f

    // Calculate initial step size for desired number of lines
    var stepSize = range / targetLineCount

    // Round to a nice value (0.1, 0.2, 0.5, 1, 2, 5, 10, etc.)
    val magnitude = 10.0.pow(floor(log10(stepSize)))
    val normalizedStep = stepSize / magnitude

    val niceStep = when {
        normalizedStep < 1.5 -> 1.0
        normalizedStep < 3.0 -> 2.0
        normalizedStep < 7.0 -> 5.0
        else -> 10.0
    } * magnitude

    return niceStep.toFloat()
}

object TestData {
    val PLN_USD = listOf(
        4.39, 4.39, 4.39, 4.38, 4.39, 4.40, 4.41, 4.41, 4.38, 4.41, 4.39, 4.40, 4.41,
        4.41, 4.42, 4.42, 4.42, 4.40, 4.41, 4.40, 4.40, 4.41, 4.41, 4.41, 4.42, 4.40,
        4.40, 4.40, 4.40, 4.39, 4.40, 4.39, 4.39, 4.39, 4.37, 4.37, 4.37, 4.38, 4.37,
        4.41, 4.40, 4.42, 4.38, 4.40, 4.38, 4.38, 4.38, 4.38, 4.38, 4.38, 4.37, 4.37,
        4.38, 4.37, 4.43, 4.42, 4.40, 4.39, 4.39, 4.40, 4.39, 4.40, 4.42, 4.47, 4.46,
        4.40, 4.38, 4.40, 4.39, 4.37, 4.37, 4.36, 4.37, 4.37, 4.36, 4.37, 4.36, 4.34
    )
    val RIVER = listOf(
        115, 93, 103, 85, 111, 89, 118, 98, 83, 108, 95, 110, 101,
        81, 113, 91, 116, 88, 105, 99, 112, 86, 107, 119, 92, 100,
        104, 87, 114, 102, 96, 109, 82, 120, 90, 84, 97, 117, 106,
        109, 101, 84, 111, 98, 117, 81, 106, 115, 92, 103, 89, 96,
        112, 104, 88, 119, 91, 85, 108, 114, 100, 95, 118, 83, 102,
        97, 110, 86, 105, 116, 93, 113, 99, 80, 107, 111, 94, 120
    )
}