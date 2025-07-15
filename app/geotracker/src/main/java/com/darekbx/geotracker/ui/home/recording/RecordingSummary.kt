package com.darekbx.geotracker.ui.home.recording

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.geotracker.repository.model.Point
import com.darekbx.geotracker.repository.model.Track
import com.darekbx.geotracker.ui.theme.GeoTrackerTheme
import com.darekbx.geotracker.ui.theme.LocalColors
import com.darekbx.geotracker.ui.theme.LocalStyles
import com.darekbx.geotracker.utils.DateTimeUtils
import com.darekbx.geotracker.utils.SpeedUtils

@Composable
fun RecordingSummary(modifier: Modifier, recordingViewModel: RecordingViewModel = hiltViewModel()) {
    val activeTrack by recordingViewModel.listenForActiveTrack().collectAsState(initial = null)
    val activePoints by recordingViewModel.listenForLocationUpdates().collectAsState(initial = null)
    SummaryWrapper(modifier, activePoints, activeTrack)
}

@Composable
fun SummaryWrapper(modifier: Modifier, points: List<Point>?, track: Track?) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                points?.firstOrNull()?.let {
                    Text(
                        text = "%.1f".format(SpeedUtils.msToKm(it.speed)),
                        style = LocalStyles.current.title,
                        fontSize = 42.sp,
                        color = LocalColors.current.red
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 6.dp),
                        text = "km/h",
                        style = LocalStyles.current.grayLabel,
                        fontSize = 20.sp,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                track?.let { track ->
                    Text(
                        text = "%.2fkm".format((track.distance ?: 0F) / 1000.0),
                        style = LocalStyles.current.grayLabel,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = DateTimeUtils.getFormattedTime(
                            (System.currentTimeMillis() - track.startTimestamp) / 1000,
                            short = true
                        ),
                        style = LocalStyles.current.grayLabel,
                        fontSize = 20.sp,
                        color = Color.LightGray
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                track?.let { track ->
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = "${points?.size ?: 0} points",
                        style = LocalStyles.current.grayLabel,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SummaryPreview() {
    GeoTrackerTheme {
        SummaryWrapper(
            modifier = Modifier,
            points = listOf(Point(100L, 0.0, 0.0, 12.4F, 100.0)),
            track = Track(1L, null, 1694770269000L, null, 12142.23F, 0)
        )
    }
}