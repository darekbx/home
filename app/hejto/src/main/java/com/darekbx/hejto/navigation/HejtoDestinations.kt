package com.darekbx.hejto.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface HejtoDestination {
    val route: String
}

object Board : HejtoDestination {
    override val route = "board"
}

object BoardByTag : HejtoDestination {
    override val route = "boardTag"
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

object BoardByCommunity : HejtoDestination {
    override val route = "boardCommunity"
    const val slugArg = "slug"
    val routeWithArgs = "${route}?$slugArg={${slugArg}}"
    val arguments = listOf(
        navArgument(slugArg) {
            nullable = true
            defaultValue = null
            type = NavType.StringType
        }
    )
}

object TagList : HejtoDestination {
    override val route = "tag_list"
}

object FavouriteTags : HejtoDestination {
    override val route = "favourite_tags"
}

object Settings : HejtoDestination {
    override val route = "settings"
}

object Saved : HejtoDestination {
    override val route = "saved"
}

object Communities : HejtoDestination {
    override val route = "communites"
}

object Post : HejtoDestination {
    override val route = "post"
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
