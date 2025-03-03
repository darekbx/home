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

            val height = containerSize.height
            val width = containerSize.width
            val widthRatio = width / (count - 1).toFloat()
            val heightRatio = height / (maxValue - minValue)

            var firstPoint = Offset(
                0F,
                (height - ((data.first() - minValue) * heightRatio)).toFloat()
            )

            generateSequence(roundMin) { it + guideLinesStep }
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

object TestData {
    val PLN_USD = listOf(
        4.39, 4.39, 4.39, 4.38, 4.39, 4.40, 4.41, 4.41, 4.38, 4.41, 4.39, 4.40, 4.41,
        4.41, 4.42, 4.42, 4.42, 4.40, 4.41, 4.40, 4.40, 4.41, 4.41, 4.41, 4.42, 4.40,
        4.40, 4.40, 4.40, 4.39, 4.40, 4.39, 4.39, 4.39, 4.37, 4.37, 4.37, 4.38, 4.37,
        4.41, 4.40, 4.42, 4.38, 4.40, 4.38, 4.38, 4.38, 4.38, 4.38, 4.38, 4.37, 4.37,
        4.38, 4.37, 4.43, 4.42, 4.40, 4.39, 4.39, 4.40, 4.39, 4.40, 4.42, 4.47, 4.46,
        4.40, 4.38, 4.40, 4.39, 4.37, 4.37, 4.36, 4.37, 4.37, 4.36, 4.37, 4.36, 4.34
    )
}