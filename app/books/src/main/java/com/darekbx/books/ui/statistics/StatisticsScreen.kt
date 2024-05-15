@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.books.ui.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.books.data.model.StatisticsItem
import com.darekbx.books.ui.LocalColors
import com.darekbx.common.ui.theme.HomeTheme

@Composable
fun StatisticsScreen(statisticsViewModel: StatisticsViewModel = hiltViewModel()) {
    val data by statisticsViewModel.statisticsData().collectAsState(initial = emptyList())
    StatisticsScreenView(data)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreenView(data: List<StatisticsItem>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Statistics",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    )
                },
                colors = topAppBarColors(
                    containerColor = LocalColors.current.green,
                    titleContentColor = Color.White,
                )
            )
        },
        content = { innerPadding ->
            Column(
                Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            ) {
                Row(
                    Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .height(80.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    //Chart(data)
                    BooksChart(modifier = Modifier.fillMaxSize(), data = data)
                }

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(data) { statisticsItem ->
                        Column {
                            StatisticsItemView(statisticsItem)
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun Chart(items: List<StatisticsItem>) {
    val heightRatio = 5F
    val step = 4F
    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val widthStep = (size.width - step) / (items.size)
        var start = step

        items.reversed().forEach { item ->

            drawRect(
                color = Color(0xFF0A247D),
                topLeft = Offset(start, 0F),
                size = Size(widthStep - step, (item.polish + item.english) * heightRatio)
            )
            drawRect(
                color = Color(0xFFE75B52),
                topLeft = Offset(start, 0F),
                size = Size(widthStep - step, item.polish * heightRatio)
            )

            /*drawRect(Color(0xFF0A247D), Rect.fromLTRB(
                start, item.polish * heightRatio, start + widthStep, 0),
                paintTypePolish);*/
            start += widthStep
        }
    }
}

@Composable
private fun StatisticsItemView(statisticsItem: StatisticsItem) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("${statisticsItem.year}")
                }
            },
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.width(30.dp),
                text = "${statisticsItem.count}",
                textAlign = TextAlign.Right,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                color = Color.Black
            )
            Text(
                modifier = Modifier.width(30.dp),
                text = "${statisticsItem.polish}",
                textAlign = TextAlign.Right,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                color = LocalColors.current.red
            )
            Text(
                modifier = Modifier.width(30.dp),
                text = "${statisticsItem.english}",
                textAlign = TextAlign.Right,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                color = LocalColors.current.blue
            )
        }
    }
}

val TEST_DATA = listOf(
    StatisticsItem(2011, 28, 0, 28),
    StatisticsItem(2012, 43, 0, 43),
    StatisticsItem(2013, 59, 0, 59),
    StatisticsItem(2014, 53, 0, 53),
    StatisticsItem(2015, 50, 1, 49),
    StatisticsItem(2016, 36, 2, 34),
    StatisticsItem(2017, 46, 12, 34),
    StatisticsItem(2018, 20, 5, 15),
    StatisticsItem(2019, 20, 13, 7),
    StatisticsItem(2020, 24, 20, 4),
    StatisticsItem(2021, 28, 21, 7),
    StatisticsItem(2022, 25, 18, 7),
    StatisticsItem(2023, 28, 12, 16),
    StatisticsItem(2024, 15, 2, 13)
)

@Composable
fun BooksChart(
    modifier: Modifier = Modifier,
    data: List<StatisticsItem>
) {
    val count = data.count()
    val maxValue = remember { (data.maxOfOrNull { it.count.toDouble() } ?: 1.0) }
    val minValue = 0.0

    val black = Color(0xFF333333)
    val blue = LocalColors.current.blue
    val red = LocalColors.current.red

    val brush = remember { Brush.verticalGradient(listOf(black, black.copy(alpha = 0.3F))) }
    val brushPl = remember { Brush.verticalGradient(listOf(red, red.copy(alpha = 0.3F))) }
    val brushEn = remember { Brush.verticalGradient(listOf(blue, blue.copy(alpha = 0.3F))) }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val height = size.height
            val width = size.width
            val widthRatio = width / (count - 1).toFloat()
            val heightRatio = height / (maxValue - minValue)

            val definitions = Definitions(width, height, widthRatio, heightRatio, minValue)
            drawPath(path = createPath(definitions) { i -> getCountOrNull(data, i) }, brush)
            drawPath(path = createPath(definitions) { i -> getPolishOrNull(data, i) }, brushPl)
            drawPath(path = createPath(definitions) { i -> getEnglishOrNull(data, i) }, brushEn)
        }
    }
}

data class Definitions(
    val width: Float,
    val height: Float,
    val widthRatio: Float,
    val heightRatio: Double,
    val minValue: Double
)

private fun getEnglishOrNull(
    data: List<StatisticsItem>,
    index: Int
) = data.getOrNull(index)?.english?.toFloat()

private fun getPolishOrNull(
    data: List<StatisticsItem>,
    index: Int
) = data.getOrNull(index)?.polish?.toFloat()

private fun getCountOrNull(
    data: List<StatisticsItem>,
    index: Int
) = data.getOrNull(index)?.count?.toFloat()

fun createPath(
    definitions: Definitions,
    dataParam: (Int) -> Float?
): Path {
    val firstPoint = Offset(
        0F,
        (definitions.height - (((dataParam(0)
            ?: 1F) - definitions.minValue) * definitions.heightRatio)).toFloat()
    )

    return with(definitions) {
        Path().apply {
            moveTo(0F, height)
            lineTo(firstPoint.x, firstPoint.y)

            var param: Float?
            var index = 0

            do {
                param = dataParam(index) ?: break
                val x = (index * widthRatio)
                val y = height - ((param - minValue) * heightRatio).toFloat()
                lineTo(x, y)
                index++
            } while (true)

            lineTo(width, height)
        }
    }
}

@Preview
@Composable
fun BooksChartPreview() {
    HomeTheme(isDarkTheme = false) {
        Surface {
            StatisticsScreenView(data = TEST_DATA)
        }
    }
}
