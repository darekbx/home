package com.darekbx.geotracker.ui.statistics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel

class StatisticsState(
    private val statisticsViewModel: StatisticsViewModel
)  {
    val state: StatisticsUiState
        @Composable get() = statisticsViewModel.uiState.collectAsState(initial = StatisticsUiState.Idle).value

    fun loadStatistics() = statisticsViewModel.loadStatistics()
}

@Composable
fun rememberStatisticsViewState(
    viewModel: StatisticsViewModel = hiltViewModel()
) = remember {
    StatisticsState(viewModel)
}