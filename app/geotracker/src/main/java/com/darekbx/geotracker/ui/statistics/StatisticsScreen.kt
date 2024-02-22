package com.darekbx.geotracker.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.theme.GeoTrackerTheme
import com.darekbx.geotracker.ui.trips.YearsScroller
import com.darekbx.geotracker.ui.trips.states.YearsViewState
import com.darekbx.geotracker.ui.trips.states.rememberYearsViewState
import com.darekbx.geotracker.ui.trips.viewmodels.YearsUiState

@Composable
fun StatisticsScreen(
    statisticsState: StatisticsState = rememberStatisticsViewState(),
    yearsViewState: YearsViewState = rememberYearsViewState()
) {
    var year by remember { mutableIntStateOf(yearsViewState.currentYear()) }
    val state = statisticsState.state

    LaunchedEffect(year) {
        statisticsState.loadStatistics(year)
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
                ) { selectedYear -> year = selectedYear }

                YearsUiState.InProgress -> LoadingProgress(Modifier.padding(4.dp).size(32.dp))

                YearsUiState.Idle -> { }
            }
        }

        state.let {
            when (it) {
                is StatisticsUiState.Done -> StatisticsView(/* TODO add data */)

                StatisticsUiState.InProgress -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { LoadingProgress() }

                StatisticsUiState.Idle -> {}
            }
        }
    }
}

@Composable
fun StatisticsView(/* TODO add data */) {

    // TODO
}

@Composable
fun StatisticsViewPreview() {
    GeoTrackerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            StatisticsView()
        }
    }
}
