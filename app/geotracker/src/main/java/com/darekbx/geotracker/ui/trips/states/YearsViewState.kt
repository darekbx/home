package com.darekbx.geotracker.ui.trips.states

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.geotracker.ui.trips.viewmodels.YearsUiState
import com.darekbx.geotracker.ui.trips.viewmodels.YearsViewModel

class YearsViewState(
    private val viewModel: YearsViewModel
)  {
    val state: YearsUiState
        @Composable get() = viewModel.uiState.collectAsState(initial = YearsUiState.Idle).value

    fun loadYears() = viewModel.loadYears()

    fun currentYear() = viewModel.currentYear()
}

@Composable
fun rememberYearsViewState(
    viewModel: YearsViewModel = hiltViewModel()
) = remember {
    YearsViewState(viewModel)
}