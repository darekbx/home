package com.darekbx.geotracker.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.darekbx.common.ui.theme.MontserratFontFamily

class Styles {

    val title = TextStyle(
        fontFamily = MontserratFontFamily,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (0.5).sp
    )
    val boldLabel = TextStyle(
        fontFamily = MontserratFontFamily,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (0.5).sp
    )
    val grayLabel = TextStyle(
        fontFamily = MontserratFontFamily,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        color = Color.Gray,
        letterSpacing = (0.5).sp
    )
}

val LocalStyles = compositionLocalOf { Styles() }