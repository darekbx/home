package com.darekbx.spreadsheet.navigation

import android.os.Bundle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

object RouteSheets {
    const val route = "sheets"
}

object RouteSheet {
    private const val uidArg = "uid"
    const val route = "sheet"
    val routeWithArgs = "$route/{$uidArg}"
    val arguments: List<NamedNavArgument> = listOf(
        navArgument(uidArg) { type = NavType.StringType }
    )
    fun build(uid: String) = "$route/$uid"
    fun parse(arguments: Bundle) = arguments.getString(uidArg)
}