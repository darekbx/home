package com.darekbx.lifetimememo.commonui.theme

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val ColorPalette = darkColorScheme(
    primary = Highlight,
    secondary = Secondary,
    background = Background,
    surface = OnSurface,
    onPrimary = Secondary,
    onSecondary = TextColor,
    onBackground = TextColor,
    onSurface = TextColor
)

object Paddings {
    val Small = 4.dp
    val Default = 8.dp
    val Big = 16.dp
}

object RoundedCorners {
    val Default = 8.dp
}

@Composable
fun LifeTimeMemoTheme(content: @Composable () -> Unit) {
    val colors = ColorPalette
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showSystemUi = true, device = Devices.PIXEL_2_XL)
@Composable
private fun LightThemePreview() {
    LifeTimeMemoTheme {
        Surface(modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(OnSurface)
        ) {
            Row(
                modifier = Modifier.size(100.dp)
                    .border(
                        border = BorderStroke(1.dp, Highlight),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = "Light theme!"
                )
                FloatingActionButton(
                    modifier = Modifier.padding(4.dp),
                    onClick = { }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "")
                }
            }
        }
    }
}

