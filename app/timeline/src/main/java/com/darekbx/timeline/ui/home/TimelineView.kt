package com.darekbx.timeline.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.timeline.model.Category
import com.darekbx.timeline.model.Entry
import com.darekbx.timeline.ui.TimeUtils
import com.darekbx.timeline.ui.theme.CategoryColors
import java.util.concurrent.TimeUnit

@Composable
fun TimelineView(modifier: Modifier, entries: List<Entry>) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val scrollState = rememberLazyListState()

        LaunchedEffect(entries.size) {
            scrollState.scrollToItem(0)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind { drawGuideLine() },
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(entries) { index, entry ->

                if (index > 0) {
                    val diff = entries[index - 1].timestamp - entry.timestamp
                    val days = TimeUnit.MILLISECONDS.toDays(diff)
                    with(LocalDensity.current) {
                        Spacer(modifier = Modifier.height(days.toInt().toDp()))
                    }
                }

                DrawTimelineEntry(entry)
            }
        }
    }
}

@Composable
private fun DrawTimelineEntry(entry: Entry) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1F))
        Spacer(
            modifier = Modifier
                .size(32.dp)
                .background(Color(entry.categoryNotNull().color), CircleShape)
                .padding(10.dp)
                .background(Color.White, CircleShape)
        )
        Text(
            modifier = Modifier
                .weight(1F)
                .padding(start = 16.dp, top = 12.dp),
            text = buildAnnotatedString {
                append(entry.title)
                withStyle(
                    style = SpanStyle(
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    )
                ) {
                    append("  (")
                    append(TimeUtils.formattedDate(entry.timestamp))
                    append(")")
                }
                append("\n")
                withStyle(
                    style = SpanStyle(
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp
                    )
                ) {
                    append(entry.description)
                }
            },
            style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        )
    }
}

private fun DrawScope.drawGuideLine() {
    val y = 0F
    val width = 5F
    val color = Color.White
    drawLine(
        Color.DarkGray,
        start = Offset(size.width / 2F, y),
        end = Offset(size.width / 2F, size.height),
        strokeWidth = 6F
    )
    drawLine(
        color,
        start = Offset(size.width / 2F - width, y),
        end = Offset(size.width / 2F - width, size.height),
        strokeWidth = 1.5F
    )
    drawLine(
        color,
        start = Offset(size.width / 2F + width, y),
        end = Offset(size.width / 2F + width, size.height),
        strokeWidth = 1.5F
    )
}

@Deprecated("Canvas based version was not rendering properly")
@Composable
fun TimelineView_Legacy(modifier: Modifier, entries: List<Entry>) {
    val measurer = rememberTextMeasurer()

    val years = entries
        .map { TimeUtils.extractYearFromTimestamp(it.timestamp) }
        .distinct()
        .sortedDescending()
    val day = 356F
    val ratio = 1F//0.65F
    val height = (years.size * day)
    val currentYear by remember {
        derivedStateOf {
            1 + TimeUtils.extractYearFromTimestamp(System.currentTimeMillis())
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height((height * ratio).dp)
                .background(Color.Black)
        ) {

            drawTimeLine(entries.last(), currentYear)

            years.forEach { year ->
                val y = (currentYear - year) * day
                drawText(
                    measurer,
                    "$year",
                    topLeft = Offset(20F, y),
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }

            entries
                .forEach { entry ->
                    val (year, dayOfYear) = TimeUtils.extractDayOfYearFromTimestamp(entry.timestamp)
                    val y = ((currentYear - year) * day - dayOfYear)
                    drawEntry(measurer, entry, y)
                }
        }
    }
}

private fun DrawScope.drawTimeLine(
    lastEntry: Entry,
    currentYear: Int
) {
    val (year, dayOfYear) = TimeUtils.extractDayOfYearFromTimestamp(lastEntry.timestamp)
    val top = ((currentYear - year) * 356F - dayOfYear)
    val width = 5F
    val color = Color.White
    drawLine(
        Color.DarkGray,
        start = Offset(size.width / 2F, top),
        end = Offset(size.width / 2F, size.height),
        strokeWidth = 6F
    )
    drawLine(
        color,
        start = Offset(size.width / 2F - width, top),
        end = Offset(size.width / 2F - width, size.height),
        strokeWidth = 1.5F
    )
    drawLine(
        color,
        start = Offset(size.width / 2F + width, top),
        end = Offset(size.width / 2F + width, size.height),
        strokeWidth = 1.5F
    )
}

private fun DrawScope.drawEntry(
    measurer: TextMeasurer,
    entry: Entry,
    y: Float
) {

    val radius = 28F
    drawCircle(
        Color(entry.categoryNotNull().color),
        radius = radius,
        center = Offset(size.width / 2F, y + radius / 2F + 8F)
    )
    drawCircle(
        Color.White,
        radius = radius / 3F,
        center = Offset(size.width / 2F, y + radius / 2F + 8F)
    )

    drawText(
        measurer,
        buildAnnotatedString {
            append(entry.title)
            withStyle(
                style = SpanStyle(
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
            ) {
                append("  (")
                append(TimeUtils.formattedDate(entry.timestamp))
                append(")")
            }
            append("\n")
            withStyle(
                style = SpanStyle(
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp
                )
            ) {
                append(entry.description)
            }
        },
        topLeft = Offset(size.width / 2F + radius * 2F, y),
        style = TextStyle(
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
    )
}


@Preview(showBackground = true)
@Composable
fun TimelinePreview() {
    TimelineView(
        modifier = Modifier,
        entries = listOf(
            Entry(1L, 1L, "Allegro", "", 1698757200000L).apply {
                category = Category(1L, "", CategoryColors[5])
            },
            Entry(1L, 1L, "Sigma", "", 1627736400000).apply {
                category = Category(1L, "", CategoryColors[5])
            },
            Entry(1L, 1L, "Millennium", "Only trial period", 1619787600000).apply {
                category = Category(1L, "", CategoryColors[5])
            },
            Entry(1L, 1L, "Cosmose", "", 1512046800000).apply {
                category = Category(1L, "", CategoryColors[5])
            },
            Entry(1L, 1L, "Mobica", "", 1448888400000).apply {
                category = Category(1L, "", CategoryColors[5])
            },
            Entry(1L, 1L, "Infor", "", 1320066000000).apply {
                category = Category(1L, "", CategoryColors[5])
            },
            Entry(1L, 2L, "Rondo", "", 1660222800000).apply {
                category = Category(1L, "", CategoryColors[2])
            },
            Entry(1L, 2L, "Commencal", "", 1683810000000).apply {
                category = Category(1L, "", CategoryColors[2])
            },
            Entry(1L, 2L, "Kona Unit", "", 1555678800000).apply {
                category = Category(1L, "", CategoryColors[2])
            },
            Entry(1L, 2L, "Trek Superfly", "", 1532523600000).apply {
                category = Category(1L, "", CategoryColors[2])
            },
        )
    )
}