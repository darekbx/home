package com.darekbx.weather.navigation

sealed class NavigationItem(
    var route: String
) {
    object Weather: NavigationItem("weather")
    object Settings: NavigationItem("settings")
}
