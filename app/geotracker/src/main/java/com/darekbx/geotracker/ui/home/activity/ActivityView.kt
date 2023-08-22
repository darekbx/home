package com.darekbx.geotracker.ui.home.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darekbx.geotracker.repository.model.ActivityData
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.defaultCard

@Composable
fun ActivityView(
    modifier: Modifier = Modifier,
    activityViewState: ActivityViewState = rememberActivityViewState()
) {
    val state = activityViewState.state
    Box(
        modifier = modifier
            .defaultCard()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is ActivityUiState.InProgress -> LoadingProgress()
            is ActivityUiState.Done -> ActivityBox(activityData = state.data)
            else -> {}
        }
    }
}

@Composable
private fun ActivityBox(
    modifier: Modifier = Modifier,
    activityData: List<ActivityData>
) {
    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {

    }
}