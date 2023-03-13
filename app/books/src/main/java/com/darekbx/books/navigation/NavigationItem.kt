package com.darekbx.books.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

sealed class NavigationItem(
    var route: String
) {
    object List: NavigationItem("list")
    object ToRead: NavigationItem("to_read")
    object Statistics: NavigationItem("statistics")
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }