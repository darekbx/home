package com.darekbx.timeline.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onCategoriesClick: () -> Unit = { },
    onNewTimelineClick: () -> Unit = { },
) {
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(onClick = onCategoriesClick) {
            Text(text = "Categories")
        }
        Button(onClick = onNewTimelineClick) {
            Text(text = "New timeline")
        }
    }
}