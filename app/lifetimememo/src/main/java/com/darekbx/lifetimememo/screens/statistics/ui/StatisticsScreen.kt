package com.darekbx.lifetimememo.screens.statistics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.lifetimememo.screens.statistics.viemodel.StatisticsViewModel

@Composable
fun StatisticsScreen(
    statisticsViewModel: StatisticsViewModel = hiltViewModel()
) {
    val containersCount by statisticsViewModel.countContainers().observeAsState()
    val memosCount by statisticsViewModel.countMemos().observeAsState()

    Column(Modifier.padding(16.dp)) {
        val containersText = buildAnnotatedString {
            append("Containers count: ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$containersCount")
            }
        }
        val memosText = buildAnnotatedString {
            append("Memos count: ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$memosCount")
            }
        }
        Text(text = containersText)
        Text(text = memosText)
    }
}