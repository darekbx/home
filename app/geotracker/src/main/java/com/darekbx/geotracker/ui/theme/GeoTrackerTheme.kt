package com.darekbx.geotracker.ui.theme

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.darekbx.common.ui.theme.MontserratFontFamily

@Composable
fun GeoTrackerTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = Color(223, 119, 76),
        secondary = Color(90, 141, 185),
        background = Color.Black,
        onBackground = Color(60, 60, 60),
        surface = Color(18, 18, 19),
        primaryContainer = Color(34, 34, 37),
        secondaryContainer = Color(60, 60, 60),
        onPrimary = Color(189, 189, 199),
        onSurface = Color(225, 225, 227)
    )
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme,
        typography = Typography(
            labelSmall = TextStyle(
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                letterSpacing = (-0.25).sp
            ),
            labelMedium = TextStyle(
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                lineHeight = 14.sp
            ),
            titleSmall = TextStyle(
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.W700,
                fontSize = 12.sp,
                lineHeight = 14.sp
            ),
            titleMedium = TextStyle(
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.5.sp
            ),
            headlineMedium = TextStyle(
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.5.sp
            )
        ),
        shapes = Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(12.dp)
        ),
        content = content
    )
}

@Preview(showSystemUi = true)
@Composable
private fun TypographyPreview() {
    GeoTrackerTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onBackground)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "titleMedium",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "labelMedium",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "labelSmall",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Text(
                        text = "6h",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall
                    )

                }
            }
        }
    }
}
