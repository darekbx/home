@file:OptIn(ExperimentalMaterialApi::class)

package com.darekbx.geotracker.ui.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.geotracker.domain.usecase.TripsWrapper
import com.darekbx.geotracker.repository.model.Track
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.defaultCard
import com.darekbx.geotracker.ui.theme.GeoTrackerTheme
import com.darekbx.geotracker.ui.theme.LocalColors
import com.darekbx.geotracker.ui.theme.LocalStyles
import com.darekbx.geotracker.ui.theme.inputColors
import com.darekbx.geotracker.ui.trips.states.TripsViewState
import com.darekbx.geotracker.ui.trips.states.YearsViewState
import com.darekbx.geotracker.ui.trips.states.rememberTripsViewState
import com.darekbx.geotracker.ui.trips.states.rememberYearsViewState
import com.darekbx.geotracker.ui.trips.viewmodels.TripsUiState
import com.darekbx.geotracker.ui.trips.viewmodels.YearsUiState
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe

@Composable
fun TripsScreen(
    modifier: Modifier = Modifier,
    tripsViewState: TripsViewState = rememberTripsViewState(),
    yearsViewState: YearsViewState = rememberYearsViewState(),
    onTrackClick: (Track) -> Unit
) {
    var year by remember { mutableIntStateOf(yearsViewState.currentYear()) }

    fun deleteTrack(track: Track) {
        tripsViewState.delete(track)
    }

    LaunchedEffect(year) {
        tripsViewState.loadTrips(year)
    }

    LaunchedEffect(Unit) {
        yearsViewState.loadYears()
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        yearsViewState.state.let {
            when (it) {
                is YearsUiState.Done -> YearsScroller(
                    modifier = Modifier.fillMaxWidth(),
                    years = it.years,
                    currentYear = year
                ) { selectedYear ->
                    year = selectedYear
                }

                YearsUiState.InProgress ->
                    LoadingProgress(
                        Modifier
                            .padding(4.dp)
                            .size(32.dp)
                    )

                YearsUiState.Idle -> {}
            }
        }

        tripsViewState.state.let {
            when (it) {
                is TripsUiState.Done -> TripsList(year, it.data, onTrackClick, ::deleteTrack)

                TripsUiState.InProgress -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { LoadingProgress() }

                TripsUiState.Idle -> {}
            }
        }
    }
}

@Composable
fun YearSummary(
    modifier: Modifier = Modifier,
    year: Int,
    distance: Double,
    count: Int,
    longestTripDistance: Float
) {
    Box(
        modifier = modifier
            .defaultCard()
            .height(80.dp)
            .padding(start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "%.1fkm".format(distance),
                        style = LocalStyles.current.title,
                        fontSize = 22.sp,
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 0.dp),
                        text = " in ",
                        style = LocalStyles.current.grayLabel,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "$year",
                        style = LocalStyles.current.title,
                        fontSize = 22.sp,
                    )
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$count",
                        style = LocalStyles.current.title,
                        fontSize = 22.sp,
                    )
                    Text(
                        text = " trips",
                        style = LocalStyles.current.grayLabel,
                        fontSize = 20.sp,
                    )
                }
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "%.1fkm".format(longestTripDistance),
                    style = LocalStyles.current.title,
                    fontSize = 20.sp,
                )
                Text(
                    text = " (longest single trip)",
                    style = LocalStyles.current.grayLabel,
                    fontSize = 22.sp,
                )
            }
        }
    }
}

@Composable
fun TripsList(
    year: Int,
    wrapper: TripsWrapper,
    onItemClick: (Track) -> Unit = { },
    onItemDeleteClick: (Track) -> Unit = { }
) {
    if (wrapper.trips.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "No trips in this year...",
                style = LocalStyles.current.grayLabel,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }

    val filter = remember { mutableStateOf("") }
    val longestTripDistance = wrapper.trips.maxBy { it.distance ?: 0F }.distance ?: 0F

    YearSummary(
        modifier = Modifier.fillMaxWidth(),
        year,
        wrapper.sumDistance,
        wrapper.trips.size,
        longestTripDistance
    )
    SearchInputField(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
        value = filter
    )
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        items(wrapper.trips.filter {
            if (filter.value.isNotBlank()) {
                it.filterById(filter.value) || it.filterByLabel(filter.value)
            } else {
                true
            }
        }) { track ->
            RevealSwipe(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .padding(start = 8.dp, end = 8.dp),
                backgroundCardEndColor = LocalColors.current.red,
                onBackgroundEndClick = { onItemDeleteClick(track) },
                directions = setOf(RevealDirection.EndToStart),
                hiddenContentEnd = {
                    Icon(
                        modifier = Modifier.padding(horizontal = 25.dp),
                        imageVector = Icons.Outlined.Delete,
                        tint = Color.White,
                        contentDescription = null
                    )
                }
            ) {
                TripListItem(modifier = Modifier
                    .padding(start = 8.dp, top = 4.dp, end = 8.dp)
                    .clickable { onItemClick(track) }, track = track)
            }
        }
    }
}

@Composable
private fun SearchInputField(
    modifier: Modifier = Modifier,
    value: MutableState<String>
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        value = value.value,
        onValueChange = { value.value = it },
        singleLine = true,
        label = { Text("Search") },
        colors = inputColors(),
    )
}

@Composable
fun TripListItem(modifier: Modifier = Modifier, track: Track) {
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
        if (track.hasPoints()) {
            Spacer(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(
                        Color(0xFF3BA732),
                        RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                    )
            )
        } else {
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(start = 0.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 0.dp),
                    text = "ID: ${track.id}",
                    style = LocalStyles.current.grayLabel,
                    fontSize = 10.sp
                )
                Text(
                    modifier = Modifier.padding(bottom = 0.dp),
                    text = track.fromattedStartTime(),
                    style = LocalStyles.current.grayLabel,
                    fontSize = 14.sp,
                    color = Color.LightGray
                )
            }

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        modifier = Modifier
                            .width(84.dp),
                        text = "%.2fkm".format(track.distance),
                        style = LocalStyles.current.title,
                        textAlign = TextAlign.End,
                        fontSize = 16.sp,
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 0.dp),
                        text = " in ",
                        style = LocalStyles.current.grayLabel,
                        fontSize = 14.sp
                    )
                    TripTime(track)
                }
                track.label()?.let { label ->
                    Text(
                        modifier = Modifier.padding(start = 18.dp),
                        text = label,
                        textAlign = TextAlign.End,
                        style = LocalStyles.current.grayLabel,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TripTime(track: Track) {
    val greyStyle = SpanStyle(color = Color.Gray)
    track.timespan()?.split(" ")?.let { chunks ->
        Text(
            text = buildAnnotatedString {
                withStyle(style = if (track.emptyHours()) greyStyle else SpanStyle()) {
                    append(chunks[0])
                }
                append(" ")
                append(chunks[1])
            },
            style = LocalStyles.current.title,
            fontSize = 16.sp,
        )
    } ?: run {
        Text(
            text = "n/a",
            style = LocalStyles.current.title,
            fontSize = 16.sp,
            color = LocalColors.current.red
        )
    }
}

@Composable
fun YearsScroller(
    modifier: Modifier = Modifier,
    years: List<Int>,
    currentYear: Int,
    onYearSelected: (Int) -> Unit = { }
) {
    Box(
        modifier = modifier
            .defaultCard(),
        contentAlignment = Alignment.CenterStart
    ) {
        LazyRow(Modifier) {
            items(years) { year ->
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
                        .clickable { onYearSelected(year) },
                    text = "$year",
                    color = if (currentYear == year) LocalColors.current.red else Color.White,
                    fontSize = 16.sp,
                    fontWeight = if (currentYear == year) FontWeight.Bold else FontWeight.Normal,
                    style = TextStyle(
                        textDecoration = if (currentYear == year) TextDecoration.Underline else TextDecoration.None
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun YearsScrollerPreview() {
    GeoTrackerTheme {
        YearsScroller(
            modifier = Modifier
                .background(Color.Black)
                .width(300.dp),
            years = listOf(2019, 2020, 2021, 2022, 2023),
            currentYear = 2023
        )
    }
}

@Preview
@Composable
fun TripListItemPreview() {
    GeoTrackerTheme {
        TripListItem(track = Track(412L, "No label", 1694279188471, 1694280291125, 46.50F, 0))
    }
}

@Preview
@Composable
fun TripListItemNoEndTimePreview() {
    GeoTrackerTheme {
        TripListItem(track = Track(412L, null, 1694279188471, null, 12.50F, 10))
    }
}

@Preview
@Composable
fun YearSummaryPreview() {
    GeoTrackerTheme {
        YearSummary(year = 2023, distance = 431241.0, count = 412, longestTripDistance = 31.0F)
    }
}