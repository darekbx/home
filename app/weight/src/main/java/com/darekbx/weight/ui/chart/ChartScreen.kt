@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.weight.ui.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.weight.R
import com.darekbx.weight.ui.WeightViewModel

@Composable
fun ChartScreen(
    weightViewModel: WeightViewModel = hiltViewModel(),
    openList: () -> Unit
) {
    val entries by weightViewModel.getEntries().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }

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
                    onClick = { openList() },
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_list),
                        contentDescription = "statistics"
                    )
                }
            }
        },
        topBar = { HeaderView(entries.size) },
        content = { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {

                ChartView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    weightEntries = entries
                )

                if (showAddDialog) {
                    NewEntryDialog(onDismiss = { showAddDialog = false })
                }

                if (entries.isEmpty()) {
                    LoadingProgress()
                }
            }
        }
    )
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

@Composable
private fun HeaderView(entriesCount: Int) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        color = Color.White,
        fontWeight = FontWeight.Light,
        text = buildAnnotatedString {
            append("Entries: ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$entriesCount")
            }
        },
        style = MaterialTheme.typography.titleSmall
    )
}
