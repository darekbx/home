package com.darekbx.vault.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument

interface NavigationItem {
    val route: String
}

object RoutePin: NavigationItem {
    override val route = "pin"
}

object RouteList: NavigationItem {
    override val route = "list"
}

object RouteSecret : NavigationItem {
    override val route = "sectet"
    const val secretIdArg = "secret_id"
    val routeWithArgs = "${route}?$secretIdArg={${secretIdArg}}"
    val arguments = listOf(
        navArgument(secretIdArg) {
            nullable = false
            defaultValue = 0L
            type = NavType.LongType
        }
    )
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
    }