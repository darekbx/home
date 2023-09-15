package com.darekbx.geotracker.ui.home.activity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel

class ActivityViewState(
    private val activityViewModel: ActivityViewModel
) {
    val state: ActivityUiState
        @Composable get() = activityViewModel.uiState.collectAsState(initial = ActivityUiState.Idle).value

    fun refresh() {
        activityViewModel.refresh()
    }
}

@Composable
fun rememberActivityViewState(
    activityViewModel: ActivityViewModel = hiltViewModel()
) = remember {
    ActivityViewState(activityViewModel)
}