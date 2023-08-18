package com.darekbx.geotracker.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class Colors(
    var orange: Color = Color(0xFFFF8F00),
    val green: Color = Color(0xFF4CAF50),
    val blue: Color = Color(0xFF0A247D),
    val red: Color = Color(0xFFE75B52),
)

val LocalColors = compositionLocalOf { Colors() }