package com.darekbx.spreadsheet.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val rowIndexHeight = 38
val cellHeight = 24

val cellStyle = TextStyle(
    fontSize = 11.sp,
    letterSpacing = 1.sp
)

val cellModifier = Modifier
    .height(cellHeight.dp)
    .border(0.25.dp, color = BORDER_COLOR)
    .padding(start = 4.dp, end = 4.dp, top = 0.dp, bottom = 0.dp)