package com.darekbx.timeline.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

class Colors(
    var orange: Color = Color(0xFFFF8F00),
    val green: Color = Color(0xFF4CAF50),
    val blue: Color = Color(0xFF0A247D),
    val red: Color = Color(0xFFE75B52),
)

val CategoryColors = listOf(
    android.graphics.Color.parseColor("#D32F2F"),
    android.graphics.Color.parseColor("#8E24AA"),
    android.graphics.Color.parseColor("#5E35B1"),
    android.graphics.Color.parseColor("#3949AB"),
    android.graphics.Color.parseColor("#388E3C"),
    android.graphics.Color.parseColor("#F57C00"),
    android.graphics.Color.parseColor("#455A64"),
)

val LocalColors = compositionLocalOf { Colors() }

@Composable
fun inputColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    errorIndicatorColor = Color.Transparent,
)
