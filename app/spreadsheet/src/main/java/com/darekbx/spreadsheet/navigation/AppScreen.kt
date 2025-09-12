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
    private const val nameArg = "name"
    const val route = "sheet"

    val routeWithArgs = "$route/{$uidArg}/{$nameArg}"
    val arguments: List<NamedNavArgument> = listOf(
        navArgument(uidArg) { type = NavType.StringType },
        navArgument(nameArg) { type = NavType.StringType }
    )

    fun build(uid: String, name: String) = "$route/$uid/$name"

    fun parse(arguments: Bundle) =
        arguments.getString(uidArg) to arguments.getString(nameArg)
}