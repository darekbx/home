package com.darekbx.fuel.navigation

sealed class NavigationItem(
    var route: String
) {
    object EntriesList: NavigationItem("list")
    object EntriesChart: NavigationItem("chart")
}
