package com.darekbx.geotracker.ui.home.summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel

class SummaryViewState(
    private val summaryViewModel: SummaryViewModel
)  {
    val state: SummaryUiState
        @Composable get() = summaryViewModel.uiState.collectAsState(initial = SummaryUiState.Idle).value

    fun refresh() {
        summaryViewModel.refresh()
    }
}

@Composable
fun rememberSummaryViewState(
    summaryViewModel: SummaryViewModel = hiltViewModel()
) = remember {
    SummaryViewState(summaryViewModel)
}