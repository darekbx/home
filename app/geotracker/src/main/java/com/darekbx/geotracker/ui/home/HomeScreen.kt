package com.darekbx.geotracker.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.darekbx.geotracker.ui.home.activity.ActivityView
import com.darekbx.geotracker.ui.home.summary.SummaryView

@Composable
fun HomeScreen() {
    Column(Modifier.fillMaxWidth()) {
        SummaryView()
        ActivityView()
        PreviewMap()
    }

    // + record button!
}


// map from last year or +10km from last trip??
@Composable
fun PreviewMap() {

}
