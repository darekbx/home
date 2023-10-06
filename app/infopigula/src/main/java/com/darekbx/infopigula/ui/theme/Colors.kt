package com.darekbx.infopigula.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class Colors(
    var black: Color = Color.Black,
    val darkblue: Color = Color(0xFF5261FB),
    val lightblue: Color = Color(0xFF3241CB),
    val red: Color = Color(0xFFD03120),
    val grey: Color = Color(0xFF737373),
)

val LocalColors = compositionLocalOf { Colors() }
