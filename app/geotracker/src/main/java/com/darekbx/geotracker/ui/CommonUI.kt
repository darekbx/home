package com.darekbx.geotracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingProgress(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer, CircleShape
            )
            .padding(8.dp)
    )
}

@Composable
fun Modifier.defaultCard(alpha: Float = 1F) = this
    .padding(top = 8.dp, start = 8.dp, end = 8.dp)
    .fillMaxWidth()
    .background(
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha),
        RoundedCornerShape(8.dp)
    )