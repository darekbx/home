package com.darekbx.timeline.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onCategoriesClick: () -> Unit = { },
) {
    var addDialogVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloatingActionButton(onClick = onCategoriesClick) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "categories")
            }
            FloatingActionButton(onClick = { addDialogVisible = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add")
            }
        }

        EntriesView()
    }

    if (addDialogVisible) {
        EntryDialog(
            onSave = { categoryId, title, description, timestamp ->
                homeViewModel.add(categoryId, title, description, timestamp)
                addDialogVisible = false
            },
            onDismiss = { addDialogVisible = false }
        )
    }
}

@Composable
private fun EntriesView(homeViewModel: HomeViewModel = hiltViewModel()) {
    Box {
        val entries by homeViewModel.entries.collectAsState(initial = emptyList())
        TimelineView(entries = entries)
    }
}
