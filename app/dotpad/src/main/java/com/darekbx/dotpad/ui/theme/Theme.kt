package com.darekbx.dotpad.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorPalette = lightColorScheme(
    primary = Color.Black,
    secondary = LightGreen,
    //secondary = Teal200,
    onSurface = Color.White
)

@Composable
fun dotpadTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    MaterialTheme(
        colorScheme = ColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}