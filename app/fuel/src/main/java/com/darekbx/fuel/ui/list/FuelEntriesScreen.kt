@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.fuel.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.ConfirmationDialog
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.fuel.R
import com.darekbx.fuel.model.FuelEntry
import com.darekbx.fuel.model.FuelType

@Composable
fun FuelEntriesScreen(
    fuelViewModel: FuelViewModel = hiltViewModel(),
    openStatistics: () -> Unit = { }
) {
    // Call this to import data for fuel from ownspace
    /*val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        fuelViewModel.importFromAssets(context)
    }*/

    val entries by fuelViewModel.entries.collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var clickedEntry by remember { mutableStateOf<FuelEntry?>(null) }

    val sumCost = entries.sumOf { it.cost }.toInt()
    val sumLiters = entries.sumOf { it.liters }.toInt()

    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
                Spacer(modifier = Modifier.height(16.dp))
                FloatingActionButton(
                    onClick = { openStatistics() },
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_statistics),
                        contentDescription = "statistics"
                    )
                }
            }
        },
        topBar = { HeaderView(entries) },
        bottomBar = { FooterView(sumCost, sumLiters) },
        content = { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(modifier = Modifier.padding(bottom = 24.dp)) {
                    items(entries, key = { it.id }) { entry ->
                        EntryView(fuelEntry = entry) {
                            clickedEntry = entry
                        }
                    }
                }

                if (showAddDialog) {
                    NewEntryDialog(onDismiss = { showAddDialog = false })
                }

                clickedEntry?.let { entry ->
                    ConfirmationDialog(
                        "Delete ${entry.cost}zł / ${entry.liters}L?",
                        "Delete",
                        onDismiss = { clickedEntry = null },
                        onConfirm = { fuelViewModel.delete(entry.id) })
                }

                if (entries.isEmpty()) {
                    LoadingProgress()
                }
            }
        }
    )
}

@Composable
private fun HeaderView(entries: List<FuelEntry>) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
        color = Color.White,
        fontWeight = FontWeight.Light,
        text = buildAnnotatedString {
            append("Entries: ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("${entries.size}")
            }
        },
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
private fun FooterView(sumCost: Int, sumLiters: Int) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
        color = Color.White,
        fontWeight = FontWeight.Light,
        text = buildAnnotatedString {
            append("Total cost: ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("${sumCost}zł")
            }
            append(", Total liters: ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("${sumLiters}L")
            }
        },
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
private fun EntryView(fuelEntry: FuelEntry, onClick: () -> Unit = { }) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id = fuelEntry.type.icon()),
                contentDescription = "type"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(
                modifier = Modifier.padding(start = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${fuelEntry.cost}zł / ${fuelEntry.liters}L",
                    fontWeight = FontWeight.W700,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "%.2fzł".format(fuelEntry.pricePerLiter()),
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Text(text = fuelEntry.date, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
fun LoadingProgress() {
    CircularProgressIndicator(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer, CircleShape
            )
            .padding(8.dp)
    )
}

@Preview(showSystemUi = true)
@Composable
fun PreviewEntryView() {
    HomeTheme(isDarkTheme = false) {
        EntryView(fuelEntry = FuelEntry(1L, "2023-02-18", 44.46, 303.66, FuelType.PB95))
    }
}
