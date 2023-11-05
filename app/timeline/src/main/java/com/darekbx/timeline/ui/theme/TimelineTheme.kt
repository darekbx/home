package com.darekbx.timeline.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.common.ui.theme.MontserratFontFamily

@Composable
fun TimelineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(223, 119, 76),
            secondary = Color(90, 141, 185),
            background = Color.Black,
            onBackground = Color(60, 60, 60),
            surface = Color(18, 18, 19),
            primaryContainer = Color(34, 34, 37),
            secondaryContainer = Color(60, 60, 60),
            onPrimary = Color.Black,
            onSurface = Color(225, 225, 227)
        ),
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
