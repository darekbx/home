package com.darekbx.stocks.ui.stocks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.stocks.model.RateInfo
import com.darekbx.stocks.model.Status
import com.darekbx.stocks.ui.settings.ProcessingView

@Composable
fun StocksScreen(
    openSettings: () -> Unit,
    stocksViewModel: StocksViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        stocksViewModel.loadRates()
    }

    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = openSettings,
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "settings")
                }
            }
        },
        content = { innerPadding ->
            val rates = stocksViewModel.rateInfoList
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val state by stocksViewModel.uiState
                when (state) {
                    UiState.InProgress -> ProcessingView(Modifier.padding(innerPadding).fillMaxHeight())
                    else -> StockCharts(Modifier.padding(innerPadding), rates)
                }
            }
        }
    )
}

@Composable
private fun StockCharts(modifier: Modifier, ratesInfos: List<RateInfo>) {
    LazyColumn(modifier.padding(top = 4.dp, bottom = 4.dp)) {
        items(ratesInfos, key = { it.label }) { ratesInfo ->
            ChartCard(
                modifier = Modifier,
                ratesInfo = ratesInfo
            )
        }
    }
}

@Preview
@Composable
private fun ChartCard(
    modifier: Modifier = Modifier,
    ratesInfo: RateInfo = RateInfo(
        TestData.PLN_USD,
        "PLN/USD",
        0.025F,
        Color(41, 182, 246),
        Status.PLUS
    )
) {
    Card(
        modifier = modifier.padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF101111)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
    ) {
        CommonChart(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(150.dp),
            data = ratesInfo.rates,
            label = ratesInfo.label,
            chartColor = ratesInfo.color.value.toLong(),
            guideLinesStep = ratesInfo.guideLinesStep,
            status = ratesInfo.status
        )
    }
}
