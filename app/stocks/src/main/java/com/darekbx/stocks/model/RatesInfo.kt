package com.darekbx.stocks.model

import androidx.compose.ui.graphics.Color

enum class Status {
    PLUS,
    MINUS,
    EQUAL
}

data class RateInfo(
    val rates: List<Double>,
    val label: String,
    val guideLinesStep: Float,
    val color: Color,
    val status: Status
)