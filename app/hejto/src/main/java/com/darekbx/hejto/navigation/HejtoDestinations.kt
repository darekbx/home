package com.darekbx.hejto.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface HejtoDestination {
    val route: String
}

object Board : HejtoDestination {
    override val route = "board"
}

object Post : HejtoDestination {
    override val route = "board"
    const val slugArg = "slug"
    val routeWithArgs = "$route?$slugArg={${slugArg}}"
    val arguments = listOf(
        navArgument(slugArg) {
            nullable = true
            defaultValue = null
            type = NavType.StringType
        }
    )
}
