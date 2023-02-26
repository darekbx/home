package com.darekbx.diggpl.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface DiggDestination {
    val route: String
}

object Homepage : DiggDestination {
    override val route = "homepage"
}

object Tags : DiggDestination {
    override val route = "tags"
}

object TagList : DiggDestination {
    override val route = "tag_list"
}

object SavedItems : DiggDestination {
    override val route = "saved_items"
}

object TagStream : DiggDestination {
    override val route = "tag_stream"
    const val tagArg = "tag"
    val routeWithArgs = "${route}?$tagArg={${tagArg}}"
    val arguments = listOf(
        navArgument(tagArg) {
            nullable = true
            defaultValue = null
            type = NavType.StringType
        }
    )
}

object Entry : DiggDestination {
    override val route = "entry"
    const val entryIdArg = "entry_id"
    val routeWithArgs = "$route?$entryIdArg={$entryIdArg}"
    val arguments = listOf(
        navArgument(entryIdArg) {
            nullable = false
            defaultValue = 0
            type = NavType.IntType
        }
    )
}

object Link : DiggDestination {
    override val route = "link"
    const val linkIdArg = "link_id"
    val routeWithArgs = "$route?$linkIdArg={$linkIdArg}"
    val arguments = listOf(
        navArgument(linkIdArg) {
            nullable = false
            defaultValue = 0
            type = NavType.IntType
        }
    )
}
