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
import androidx.compose.runtime.LaunchedEffect
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
import com.darekbx.geotracker.ui.theme.LocalColors
import com.darekbx.geotracker.ui.theme.LocalStyles
import kotlin.math.max
import kotlin.random.Random

@Composable
fun ActivityView(
    modifier: Modifier = Modifier,
    activityViewState: ActivityViewState = rememberActivityViewState(),
    openCalendar: () -> Unit = { }
) {
    val state = activityViewState.state

    LaunchedEffect(Unit) {
        activityViewState.refresh()
    }

    Box(
        modifier = modifier
            .defaultCard()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is ActivityUiState.InProgress -> LoadingProgress()
            is ActivityUiState.Done -> ActivityBox(
                activityData = state.data,
                showYearSummary = state.showYearSummary
            ) { openCalendar() }
            else -> {}
        }
    }
}

@Composable
private fun ActivityBox(
    modifier: Modifier = Modifier,
    activityData: List<ActivityData>,
    showYearSummary: Boolean,
    openCalendar: () -> Unit = { }
) {
    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Header { openCalendar() }
        Spacer(modifier = Modifier.height(12.dp))
        if (activityData.isNotEmpty()) {
            Chart(
                modifier = Modifier
                    .fillMaxSize()
                    .height(140.dp),
                data = activityData,
                showYearSummary = showYearSummary
            )
        }
    }
}

@Preview
@Composable
fun Header(openCalendar: () -> Unit = { }) {
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
                modifier = Modifier
                    .padding(top = 3.dp)
                    .offset(x = 4.dp),
                painter = painterResource(id = R.drawable.ic_arrow_right),
                tint = Color(0xFF3BA732),
                contentDescription = "right"
            )
        }
    }
}

@Composable
fun Chart(modifier: Modifier = Modifier, data: List<ActivityData>, showYearSummary: Boolean) {
    val textMeasurer = rememberTextMeasurer()
    val red = LocalColors.current.red
    Canvas(
        modifier = modifier
    ) {
        // Chart line colors
        // TODO: will throw an exception when there will be more than 6 trips during one day
        val orange = Color(0xFFE87B52)
        val yellow = Color(0xFFE7AB52)
        val yellow2 = Color(0xFFE7CB52)
        val yellow3 = Color(0xFFE7FB52)
        val yellow4 = Color(0xFFE7FBE2)
        val colors = listOf(red, orange, yellow, yellow2, yellow3, yellow4)

        val yScale = 0.85F
        val leftOffset = 50F

        val maximum = max(50.0, data.maxOf { it.sumDistance() })
        val minimum = data.minOf { it.sumDistance() }

        val count = if (showYearSummary) max(356, data.size) else data.size-1
        val widthStep = (size.width - leftOffset) / if (showYearSummary) count else count+1
        val heightStep = ((size.height - widthStep ) * yScale) / (maximum - minimum)

        var start = leftOffset

        drawLines(maximum, textMeasurer, heightStep, leftOffset)

        this.scale(1F, -1F) {
            (0..count).forEach { dayOfYear ->

                val item =
                    if (showYearSummary) data.firstOrNull { it.dayOfYear == dayOfYear }
                    else data[dayOfYear]

                if (item == null) {
                    start += widthStep
                } else {

                    var innerTop = 0.0F
                    var index = 0
                    item.distances.forEach { distance ->
                        drawRect(
                            color = colors[index],
                            topLeft = Offset(start, innerTop + 0F),
                            size = Size(widthStep, (distance * heightStep).toFloat())
                        )
                        innerTop += (distance * heightStep).toFloat()
                        index++
                    }

                    drawCircle(
                        colors[index - 1],
                        radius = widthStep / 2F,
                        Offset(start + widthStep / 2F, (item.sumDistance() * heightStep).toFloat())
                    )

                    start += widthStep
                }
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
fun ChartPreview1() {
    val r = Random(100)
    val data = listOf(
        ActivityData(1, listOf(7000.0)),
        ActivityData(2, listOf(2000.0)),
        ActivityData(3, listOf(13000.0)),
        ActivityData(3, listOf(11000.0)),
        ActivityData(3, listOf(13000.0)),
        ActivityData(1, listOf(9000.0)),
    )

    Chart(Modifier.size(200.dp, 100.dp), data, true)
}

@Preview
@Composable
fun ChartPreview2() {
    val r = Random(100)
    val data = listOf(
        ActivityData(1, listOf(10000.0)),
        ActivityData(2, listOf(20000.0)),
        ActivityData(3, listOf(26000.0)),
        ActivityData(4, listOf(65000.0)),
        ActivityData(5, listOf(12000.0)),
        ActivityData(6, listOf(8000.0, 8000.0)),
        ActivityData(7, listOf(5000.0)),
        ActivityData(8, listOf(20000.0, 20000.0, 5000.0)),
        ActivityData(9, listOf(20000.0)),
        ActivityData(10, listOf(24000.0)),
        ActivityData(11, listOf(9000.0)),
        ActivityData(12, listOf(12000.0)),
        ActivityData(13, listOf(18000.0)),
        ActivityData(14, listOf(39000.0)),
        ActivityData(15, listOf(37000.0)),
        ActivityData(16, listOf(5000.0)),
    )

    Chart(Modifier.size(200.dp, 100.dp), data, false)
}