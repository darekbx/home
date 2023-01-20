package com.darekbx.stocks.navigation

sealed class NavigationItem(
    var route: String
) {
    object Stocks: NavigationItem("stocks")
    object Settings: NavigationItem("settings")
}
