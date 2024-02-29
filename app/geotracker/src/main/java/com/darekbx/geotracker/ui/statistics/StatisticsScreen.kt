package com.darekbx.geotracker.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.geotracker.repository.model.YearSummary
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.theme.GeoTrackerTheme
import com.darekbx.geotracker.ui.theme.LocalStyles
import com.darekbx.geotracker.utils.DateTimeUtils

@Composable
fun StatisticsScreen(
    statisticsState: StatisticsState = rememberStatisticsViewState()
) {
    LaunchedEffect(Unit) {
        statisticsState.loadStatistics()
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        statisticsState.state.let { state ->
            when (state) {
                is StatisticsUiState.Done -> StatisticsView(state.yearSummaries)

                StatisticsUiState.InProgress -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { LoadingProgress() }

                StatisticsUiState.Idle -> { }
            }
        }
    }
}

@Composable
fun StatisticsView(data: List<YearSummary>) {
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        items(data) { item ->
            StatisticsItem(
                modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp),
                item = item
            )
        }
    }
}

@Composable
fun StatisticsItem(modifier: Modifier, item: YearSummary) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.CenterStart
    ) {
        val timeFormatted by remember { derivedStateOf { DateTimeUtils.getFormattedTime(item.time) } }
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                modifier = Modifier,
                text = "${item.year}",
                style = LocalStyles.current.title,
                fontSize = 24.sp,
            )
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp))
            val labelModifier = Modifier.width(180.dp)
            Row {
                Label(labelModifier, "Distance: ", "${item.distance.toInt()}km")
                Label(labelModifier, "Time: ", timeFormatted)
            }
            Row(Modifier.padding(top = 4.dp, bottom = 4.dp)) {
                Label(labelModifier, "Trips count: ", "${item.tripsCount}")
                Label(labelModifier, "Days on bike: ", "${item.daysOnBike}")
            }
            Row {
                Label(labelModifier, "Longest trip: ", "${item.longestTrip.toInt()}km")
                Label(labelModifier, "Max day distance: ", "${item.maxDayDistance.toInt()}km")
            }
        }
    }
}

@Composable
fun Label(modifier: Modifier, label:String, value: String) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.padding(end = 4.dp),
            text = label,
            style = LocalStyles.current.grayLabel,
            fontSize = 13.sp,
        )
        Text(
            modifier = Modifier,
            text = value,
            style = LocalStyles.current.title,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
        )
    }
}

@Preview
@Composable
fun StatisticsItemPreview() {
    GeoTrackerTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            StatisticsItem(Modifier.padding(8.dp), YearSummary(
                2024,
                2114.0,
                121241L,
                34,
                40,
                64.0,
                112.0
            ))
        }
    }
}
