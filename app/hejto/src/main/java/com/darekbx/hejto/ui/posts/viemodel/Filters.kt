package com.darekbx.hejto.ui.posts.viemodel

enum class PeriodFilter(val filter: String) {
    `6H`("6h"),
    `12H`("12h"),
    `24H`("24h"),
    WEEK("week"),
    MONTH("month"),
    ALL("all")
}

enum class Order(val order: String, val label: String) {
    NEWEST("p.createdAt", "Newest"),
    HOT("p.hotness", "Hot")
}
