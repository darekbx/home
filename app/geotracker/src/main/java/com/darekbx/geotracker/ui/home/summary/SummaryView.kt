package com.darekbx.geotracker.ui.home.summary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.geotracker.repository.model.Summary
import com.darekbx.geotracker.repository.model.SummaryWrapper
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.defaultCard
import com.darekbx.geotracker.ui.theme.GeoTrackerTheme
import com.darekbx.geotracker.ui.theme.LocalColors
import com.darekbx.geotracker.ui.theme.LocalStyles
import com.darekbx.geotracker.utils.DateTimeUtils

@Composable
fun SummaryView(
    modifier: Modifier = Modifier,
    summaryViewState: SummaryViewState = rememberSummaryViewState()
) {
    val state = summaryViewState.state

    LaunchedEffect(Unit) {
        summaryViewState.refresh()
    }

    Box(
        modifier = modifier
            .defaultCard()
            .height(114.dp)
            .clickable { summaryViewState.refresh() },
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is SummaryUiState.InProgress -> LoadingProgress()
            is SummaryUiState.Done -> SummaryBox(summaryWrapper = state.data, maxSpeed = state.maxSpeed)
            else -> {}
        }
    }
}

@Composable
private fun SummaryBox(
    modifier: Modifier = Modifier,
    summaryWrapper: SummaryWrapper,
    maxSpeed: Float
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = modifier
                .padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Summary",
                style = LocalStyles.current.title
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Overall",
                style = LocalStyles.current.grayLabel
            )
            SummaryRow(summaryWrapper.summary, fscale = 1.25F)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This year",
                style = LocalStyles.current.grayLabel
            )
            SummaryRow(summaryWrapper.yearSummary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Column(
            modifier = Modifier.padding(end = 8.dp),
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = "Max speed",
                style = LocalStyles.current.grayLabel
            )
            Text(
                text = "%.1f".format(maxSpeed),
                style = LocalStyles.current.title,
                fontSize = 36.sp,
                color = LocalColors.current.red
            )
            Text(
                text = "km/h",
                style = LocalStyles.current.grayLabel
            )
        }

    }
}

@Composable
private fun SummaryRow(summary: Summary, fscale: Float = 1.0F) {
    val timeFormatted by remember { derivedStateOf { DateTimeUtils.getFormattedTime(summary.time) } }

    Row(
        modifier = Modifier
            .scale(fscale)
            .offset(x = if (fscale == 1.0F) 0.dp else 23.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "${summary.distance.toInt()}km",
            style = LocalStyles.current.boldLabel
        )
        Text(
            text = "in",
            style = LocalStyles.current.grayLabel
        )
        Text(
            text = timeFormatted,
            style = LocalStyles.current.boldLabel
        )
        Text(
            text = " trips: ",
            style = LocalStyles.current.grayLabel
        )
        Text(
            text = "${summary.tripsCount}",
            style = LocalStyles.current.boldLabel
        )
    }
}


@Preview
@Composable
fun SummaryPreview() {
    GeoTrackerTheme {
        SummaryBox(
            summaryWrapper = SummaryWrapper(
                Summary(5213.27, 8287800, 213),
                Summary(2430.02, 21004, 81),
            ),
            maxSpeed = 52.4F
        )
    }
}

@Preview
@Composable
fun SummaryProgressPreview() {
    GeoTrackerTheme {
        LoadingProgress()
    }
}
