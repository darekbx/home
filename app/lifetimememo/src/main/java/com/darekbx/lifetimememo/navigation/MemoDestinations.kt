package com.darekbx.lifetimememo.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface MemoDestination {
    val route: String
    val label: String
}

object Search : MemoDestination {
    override val route = "search"
    override val label = "Search"
}

object Statistics : MemoDestination {
    override val route = "statistics"
    override val label = "Statistics"
}

object Memos : MemoDestination {
    override val route = "memos"
    override val label = "Memos"
    const val parentIdArg = "parent_id"
    val routeWithArgs = "$route?$parentIdArg={${parentIdArg}}"
    val arguments = listOf(
        navArgument(parentIdArg) {
            nullable = true
            defaultValue = null
            type = NavType.StringType
        }
    )
}

object Settings : MemoDestination {
    override val route = "settings"
    override val label = "Settings"
}

object Categories : MemoDestination {
    override val route = "categories"
    override val label = "Categories"
}

object Memo : MemoDestination {
    override val route = "memo"
    override val label = "Memo"
    const val memoIdArg = "memo_id"
    const val parentIdArg = "parent_id"
    val routeWithArgs = "${route}?$memoIdArg={$memoIdArg}&$parentIdArg={$parentIdArg}"
    val arguments = listOf(
        navArgument(memoIdArg) {
            nullable = true
            defaultValue = null
            type = NavType.StringType
        },
        navArgument(parentIdArg) {
            nullable = true
            defaultValue = null
            type = NavType.StringType
        }
    )
}

object Container : MemoDestination {
    override val route = "container"
    override val label = "Container"
    const val parentIdArg = "parent_id"
    val routeWithArgs = "$route?$parentIdArg={$parentIdArg}"
    val parentIdRouteFormat = "$route?parent_id=%s"
    val arguments = listOf(
        navArgument(parentIdArg) {
            nullable = true
            type = NavType.StringType
        }
    )
}

val appTabRowScreens = listOf(Memos, Search, Statistics, Settings)
