@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.books.ui.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.books.data.model.StatisticsItem
import com.darekbx.books.ui.LocalColors

@Composable
fun StatisticsScreen(statisticsViewModel: StatisticsViewModel = hiltViewModel()) {
    val data by statisticsViewModel.statisticsData().collectAsState(initial = emptyList())

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
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    titleContentColor = Color.White,
                    containerColor = LocalColors.current.green
                )
            )
        },
        content = { innerPadding ->
            Column(
                Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .rotate(180F),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Chart(data)
                }

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(data) { statisticsItem ->
                        StatisticsItemView(statisticsItem)
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
            .height(140.dp)
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
            .padding(start = 8.dp, end = 8.dp, top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("${statisticsItem.year}")
                }
                append(" (${statisticsItem.count})")
            },
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.width(20.dp),
                text = "${statisticsItem.polish}",
                textAlign = TextAlign.Right,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                color = LocalColors.current.red
            )
            Text(
                text = " / ",
                fontWeight = FontWeight.Light,
                fontSize = 14.sp
            )
            Text(
                modifier = Modifier.width(22.dp),
                text = "${statisticsItem.english}",
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                color = LocalColors.current.blue
            )
        }
    }
}
