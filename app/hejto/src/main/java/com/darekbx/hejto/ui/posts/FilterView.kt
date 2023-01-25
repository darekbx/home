package com.darekbx.hejto.ui.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.hejto.ui.HejtoTheme
import com.darekbx.hejto.ui.posts.viemodel.Order
import com.darekbx.hejto.ui.posts.viemodel.PeriodFilter

@Composable
fun FilterView(
    activePeriod: PeriodFilter = PeriodFilter.`6H`,
    activeOrder: Order = Order.NEWEST,
    onPeriodChanged: (PeriodFilter) -> Unit = { },
    onOrderChanged: (Order) -> Unit = { }
) {
    FiltersContent(activePeriod, activeOrder, onPeriodChanged, onOrderChanged)
}

@Composable
private fun FiltersContent(
    activePeriod: PeriodFilter = PeriodFilter.`6H`,
    activeOrder: Order = Order.NEWEST,
    onPeriodChanged: (PeriodFilter) -> Unit = { },
    onOrderChanged: (Order) -> Unit = { }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.Center
    ) {
        OrderView(activeOrder, onOrderChanged)
        Spacer(modifier = Modifier.width(8.dp))
        PeriodView(activePeriod, onPeriodChanged)
    }
}

@Composable
private fun OrderView(
    activeOrder: Order,
    onOrderChanged: (Order) -> Unit = { }
) {
    Row(
        modifier = Modifier
            .padding(start = 8.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(4.dp)
            )
            .padding(6.dp)
    ) {
        Order.values().forEach { order ->
            Text(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .clickable { onOrderChanged(order) },
                text = order.label,
                style = MaterialTheme.typography.titleMedium,
                color = if (order == activeOrder)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun PeriodView(
    activePeriodFilter: PeriodFilter = PeriodFilter.`6H`,
    onPeriodChange: (PeriodFilter) -> Unit = { },
) {
    Row(
        modifier = Modifier
            .padding(end = 8.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(4.dp)
            )
            .padding(6.dp)
    ) {
        PeriodFilter.values().forEach { period ->
            Text(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .clickable { onPeriodChange(period) },
                text = period.filter.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = if (period == activePeriodFilter)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Preview(backgroundColor = 0L)
@Composable
fun FilterViewPreview(
    activePeriod: PeriodFilter = PeriodFilter.`6H`,
    activeOrder: Order = Order.NEWEST,
    onPeriodChanged: (PeriodFilter) -> Unit = { },
    onOrderChanged: (Order) -> Unit = { }
) {
    HejtoTheme {
        FiltersContent(activePeriod, activeOrder, onPeriodChanged, onOrderChanged)
    }
}
