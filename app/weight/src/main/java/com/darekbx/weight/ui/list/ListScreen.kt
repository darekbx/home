package com.darekbx.weight.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.weight.data.model.EntryType
import com.darekbx.weight.data.model.WeightEntry
import com.darekbx.weight.data.model.dateFormat
import com.darekbx.weight.ui.WeightViewModel
import com.darekbx.weight.ui.chart.LoadingProgress

@Composable
fun ListScreen(weightViewModel: WeightViewModel = hiltViewModel()) {
    val entries by weightViewModel.getEntries().collectAsState(initial = emptyList())
    var clickedEntry by remember { mutableStateOf<WeightEntry?>(null) }

    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(modifier = Modifier.padding(bottom = 24.dp)) {
            items(entries.reversed(), key = { it.id!! }) { entry ->
                EntryView(weightEntry = entry) {
                    clickedEntry = entry
                }
            }
        }

        clickedEntry?.let { entry ->
            ConfirmationDialog(
                "Delete ${entry.weight}kg for ${entry.type.name}",
                "Delete",
                onDismiss = { clickedEntry = null },
                onConfirm = { weightViewModel.delete(entry.id) })
        }

        if (entries.isEmpty()) {
            LoadingProgress()
        }
    }
}

@Composable
fun EntryView(weightEntry: WeightEntry, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = "%.2fkg".format(weightEntry.weight),
                fontWeight = FontWeight.W700,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateFormat.format(weightEntry.date),
                color = Color.DarkGray,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Text(
            text = WeightEntry.typeNameFormatted(weightEntry.type),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Preview
@Composable
fun EntryViewPreview() {
    HomeTheme {
        EntryView(WeightEntry(0L, System.currentTimeMillis(), 54.4, EntryType.DAREK)) { }
    }
}
