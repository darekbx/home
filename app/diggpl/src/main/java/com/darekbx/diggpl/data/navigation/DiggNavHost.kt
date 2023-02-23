package com.darekbx.diggpl.data.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.diggpl.data.remote.ResourceType
import com.darekbx.diggpl.ui.homepage.HomePageScreen
import com.darekbx.diggpl.ui.link.LinkScreen
import com.darekbx.diggpl.ui.entry.EntryScreen
import com.darekbx.diggpl.ui.stream.StreamScreen

@Composable
fun DiggNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Homepage.route,
        modifier = modifier
    ) {
        composable(route = Homepage.route) {
            HomePageScreen { streamItem ->
                val id = streamItem.id
                when (streamItem.resource) {
                    ResourceType.ENTRY.type -> navController.navigate("${Entry.route}?${Entry.entryIdArg}=$id")
                    ResourceType.LINK.type -> navController.navigate("${Link.route}?${Link.linkIdArg}=$id")
                }
            }
        }

        composable(route = Tags.route) {
            StreamScreen { streamItem ->
                val id = streamItem.id
                when (streamItem.resource) {
                    ResourceType.ENTRY.type -> navController.navigate("${Entry.route}?${Entry.entryIdArg}=$id")
                    ResourceType.LINK.type -> navController.navigate("${Link.route}?${Link.linkIdArg}=$id")
                }
            }
        }

        composable(
            route = Entry.routeWithArgs,
            arguments = Entry.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getInt(Entry.entryIdArg)?.let { entryId ->
                EntryScreen(entryId)
            }
        }

        composable(
            route = Link.routeWithArgs,
            arguments = Link.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getInt(Link.linkIdArg)?.let { linkId ->
                LinkScreen(linkId)
            }
        }

    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
