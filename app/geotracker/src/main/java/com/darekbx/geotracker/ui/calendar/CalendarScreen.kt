package com.darekbx.geotracker.ui.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.geotracker.domain.usecase.TripsWrapper
import com.darekbx.geotracker.repository.model.Track
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.theme.GeoTrackerTheme
import com.darekbx.geotracker.ui.theme.LocalColors
import com.darekbx.geotracker.ui.trips.YearsScroller
import com.darekbx.geotracker.ui.trips.states.TripsViewState
import com.darekbx.geotracker.ui.trips.states.YearsViewState
import com.darekbx.geotracker.ui.trips.states.rememberTripsViewState
import com.darekbx.geotracker.ui.trips.states.rememberYearsViewState
import com.darekbx.geotracker.ui.trips.viewmodels.TripsUiState
import com.darekbx.geotracker.ui.trips.viewmodels.YearsUiState
import io.github.boguszpawlowski.composecalendar.CalendarState
import io.github.boguszpawlowski.composecalendar.StaticCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.selection.EmptySelectionState
import java.time.DayOfWeek
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun CalendarScreen(
    tripsViewState: TripsViewState = rememberTripsViewState(),
    yearsViewState: YearsViewState = rememberYearsViewState()
) {

    var year by remember { mutableIntStateOf(yearsViewState.currentYear()) }

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
                is YearsUiState.Done ->
                    YearsScroller(
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
                is TripsUiState.Done -> YearCalendar(Modifier.fillMaxWidth(), year, it.data)

                TripsUiState.InProgress -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { LoadingProgress() }

                TripsUiState.Idle -> {}
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun YearCalendar(
    modifier: Modifier,
    year: Int,
    wrapper: TripsWrapper
) {
    val months = wrapper.trips.groupBy {
        Calendar.getInstance()
            .apply { timeInMillis = it.startTimestamp }
            .get(Calendar.MONTH)
    }
    val daysOnBike = wrapper.trips.countDays()

    LazyColumn(modifier) {
        stickyHeader {
            Text(
                modifier = Modifier.background(Color.Black).fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center,
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = LocalColors.current.red
                        )
                    ) {
                        append("$daysOnBike ")
                    }
                    append("days on bike")
                },
                fontSize = 24.sp,
                color = Color.White,
            )
        }
        items(months.keys.toList()) { month ->
            val distance = months[month]
                ?.sumOf { (it.distance ?: 0F).toDouble() }
                ?: 0.0
            val tripsCount = months[month]?.size ?: 0
            val daysRidden = months[month]?.countDays() ?: 0
            StaticCalendar(
                modifier = Modifier.fillMaxWidth(),
                calendarState = CalendarState(
                    MonthState(YearMonth.of(year, month + 1)),
                    EmptySelectionState
                ),
                firstDayOfWeek = DayOfWeek.MONDAY,
                dayContent = { dayState ->
                    val trips = months[month]?.filterByDay(dayState) ?: emptyList()
                    SingleDay(Modifier, dayState, trips)
                },
                showAdjacentMonths = false,
                horizontalSwipeEnabled = false,
                monthHeader = { monthState -> MonthHeader(month, distance, tripsCount, daysRidden) },
                daysOfWeekHeader = { daysOfWeek -> WeekHeader(daysOfWeek) }
            )
        }
    }
}

@Composable
private fun WeekHeader(daysOfWeek: List<DayOfWeek>) {
    Row {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ROOT),
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
            )
        }
    }
}

@Composable
private fun MonthHeader(month: Int, distance: Double, trips: Int, days: Int) {
    Text(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        text = "${Month.of(month + 1).name} (%.1fkm) $trips trips in $days days".format(distance),
        color = Color.White,
    )
}

@Composable
private fun SingleDay(
    modifier: Modifier,
    dayState: DayState<EmptySelectionState>,
    trips: List<Track>
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        val sumDistance by remember {
            mutableDoubleStateOf(trips.sumOf {
                (it.distance ?: 0F).toDouble()
            })
        }
        Box(
            Modifier
                .padding(1.dp)
                .fillMaxHeight()
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(obtainColor(sumDistance))
        ) {
            if (dayState.isFromCurrentMonth && trips.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp),
                    text = "%.1fkm".format(sumDistance),
                    color = Color.White,
                    fontSize = 10.sp
                )
            }

            DayNumber(Modifier.align(Alignment.Center), dayState)
        }
    }
}


@Composable
private fun DayNumber(modifier: Modifier, dayState: DayState<EmptySelectionState>) {
    val weight = when (dayState.isCurrentDay) {
        true -> FontWeight.Bold
        else -> FontWeight.Normal
    }
    Text(
        modifier = modifier,
        text = "${dayState.date.dayOfMonth}",
        fontWeight = weight,
        color = when {
            dayState.isCurrentDay -> LocalColors.current.red
            dayState.isFromCurrentMonth -> Color.White
            else -> Color.DarkGray
        }
    )
}

@Preview
@Composable
fun CalendarPreview() {
    val data = TripsWrapper(
        100.0,
        listOf(
            Track(0, null, System.currentTimeMillis(), distance = 10F, pointsCount = 2),
            Track(
                0,
                null,
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(13),
                distance = 27F,
                pointsCount = 2
            ),
            Track(
                0,
                null,
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(5),
                distance = 7F,
                pointsCount = 2
            ),
            Track(
                0,
                null,
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2),
                distance = 20F,
                pointsCount = 2
            ),
            Track(
                0,
                null,
                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3),
                distance = 15F,
                pointsCount = 2
            )
        )
    )
    GeoTrackerTheme {
        YearCalendar(Modifier.fillMaxWidth(), 2024, data)
    }
}

fun obtainColor(sumDistance: Double) = when {
    sumDistance > 0 && sumDistance <= 10 -> Color(40, 220, 80, 60)
    sumDistance > 10 && sumDistance <= 20 -> Color(40, 220, 80, 100)
    sumDistance > 20 && sumDistance <= 30 -> Color(40, 220, 80, 140)
    sumDistance > 30 -> Color(40, 220, 80, 180)
    else -> Color.Black
}

fun List<Track>.filterByDay(
    dayState: DayState<EmptySelectionState>
) = filter {
    val timeStamp = Calendar.getInstance().apply { timeInMillis = it.startTimestamp }
    val year = timeStamp.get(Calendar.YEAR)
    val day = timeStamp.get(Calendar.DAY_OF_YEAR)
    year == dayState.date.year && day == dayState.date.dayOfYear
}

private fun List<Track>.countDays() = groupBy {
    val timeStamp = Calendar.getInstance().apply { timeInMillis = it.startTimestamp }
    val day = timeStamp.get(Calendar.DAY_OF_YEAR)
    day
}.count()
