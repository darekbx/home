package com.darekbx.dotpad.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darekbx.dotpad.ui.dots.toColor
import com.darekbx.dotpad.ui.theme.dotOrange

@Composable
fun CommonLoading() {
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        CircularProgressIndicator(
            Modifier
                .align(Alignment.Center)
                .size(64.dp),
            color = dotOrange.toColor()
        )
    }
}