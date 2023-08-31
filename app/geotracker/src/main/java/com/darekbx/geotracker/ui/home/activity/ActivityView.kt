package com.darekbx.geotracker.ui.home.activity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.geotracker.R
import com.darekbx.geotracker.repository.model.ActivityData
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.defaultCard
import com.darekbx.geotracker.ui.theme.LocalStyles
import kotlin.random.Random

@Composable
fun ActivityView(
    modifier: Modifier = Modifier,
    activityViewState: ActivityViewState = rememberActivityViewState()
) {
    val state = activityViewState.state
    Box(
        modifier = modifier
            .defaultCard()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is ActivityUiState.InProgress -> LoadingProgress()
            is ActivityUiState.Done -> ActivityBox(activityData = state.data)
            else -> {}
        }
    }
}

@Composable
private fun ActivityBox(
    modifier: Modifier = Modifier,
    activityData: List<ActivityData>
) {
    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Header()
        Spacer(modifier = Modifier.height(12.dp))
        Chart(
            modifier = Modifier
                .fillMaxSize()
                .height(140.dp),
            data = activityData
        )
    }
}

@Preview
@Composable
private fun Header(openCalendar: () -> Unit = { }) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Daily distances ",
                style = LocalStyles.current.title
            )
            Text(
                text = "(last year)",
                style = LocalStyles.current.title,
                color = Color.Gray
            )
        }
        Row(
            modifier = Modifier.clickable { openCalendar() },
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.offset(x = 8.dp),
                text = "Calendar",
                style = LocalStyles.current.title,
                color = Color(0xFF3BA732)
            )
            Icon(
                modifier = Modifier.padding(top = 3.dp).offset(x = 4.dp),
                painter = painterResource(id = R.drawable.ic_arrow_right),
                tint = Color(0xFF3BA732),
                contentDescription = "right"
            )
        }
    }
}

@Composable
fun Chart(modifier: Modifier = Modifier, data: List<ActivityData>) {
    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = modifier
    ) {
        val drawLine = false
        val red = Color(0xFFE75B52)
        val itemsToMark = 10
        val yScale = 0.85F
        val leftOffset = 50F

        val maximum = data.maxOf { it.distance }
        val minimum = data.minOf { it.distance }

        val highestItems = data
            .sortedByDescending { it.distance }
            .take(itemsToMark) + data.first() + data.last()

        val widthStep = (size.width - leftOffset) / data.size
        val heightStep = (size.height * yScale) / (maximum - minimum)

        var prevHighest: Offset? = null
        var start = leftOffset

        drawLines(maximum, textMeasurer, heightStep, leftOffset)

        this.scale(1F, -1F) {
            data.forEach { item ->
                if (drawLine) {
                    highestItems
                        .firstOrNull { it.dayOfYear == item.dayOfYear }
                        ?.let { highestItem ->
                            prevHighest?.let {
                                drawLine(
                                    Color(0xAAE75B52),
                                    it,
                                    Offset(start, (highestItem.distance * heightStep).toFloat())
                                )
                            }
                            prevHighest = Offset(
                                start + widthStep / 2F,
                                (highestItem.distance * heightStep).toFloat()
                            )
                        }
                }

                drawRect(
                    color = red,
                    topLeft = Offset(start, 0F),
                    size = Size(widthStep, (item.distance * heightStep).toFloat())
                )
                drawCircle(
                    red,
                    radius = widthStep / 2F,
                    Offset(start + widthStep / 2F, (item.distance * heightStep).toFloat())
                )

                start += widthStep
            }
        }
    }
}

private fun DrawScope.drawLines(
    maximum: Double,
    textMeasurer: TextMeasurer,
    heightStep: Double,
    leftOffset: Float
) {
    val gridStep = 10000
    for (value in ((maximum / gridStep).toInt() * gridStep) downTo 1 step gridStep) {
        drawText(
            textMeasurer,
            "${(value / 1000)}",
            Offset(5F, size.height - (value * heightStep).toFloat() - 18F),
            style = TextStyle.Default.copy(fontSize = 9.sp, color = Color.White)
        )
        drawLine(
            Color.DarkGray,
            Offset(leftOffset, size.height - (value * heightStep).toFloat()),
            Offset(size.width, size.height - (value * heightStep).toFloat()),
        )
    }
    drawLine(
        Color.DarkGray,
        Offset(leftOffset, 0F),
        Offset(leftOffset, size.height),
    )
    drawText(
        textMeasurer,
        "km",
        Offset(5F, 0F),
        style = TextStyle.Default.copy(fontSize = 9.sp, color = Color.Gray)
    )
}

@Preview
@Composable
fun ChartPreview() {
    val r = Random(100)
    val randomData = (0..100)
        .map { r.nextDouble() * 54120.0 }
        .mapIndexed { i, d -> ActivityData(i, d + 2100.0) }

    val data = listOf(
        ActivityData(1, 10000.0),
        ActivityData(2, 20000.0),
        ActivityData(3, 26000.0),
        ActivityData(4, 65000.0),
        ActivityData(5, 12000.0),
        ActivityData(6, 16000.0),
        ActivityData(7, 5000.0),
        ActivityData(8, 45000.0),
        ActivityData(9, 20000.0),
        ActivityData(10, 24000.0),
        ActivityData(11, 9000.0),
        ActivityData(12, 12000.0),
        ActivityData(13, 18000.0),
        ActivityData(14, 39000.0),
        ActivityData(15, 37000.0),
        ActivityData(16, 5000.0),
    )

    Chart(Modifier.size(200.dp, 100.dp), data)
}