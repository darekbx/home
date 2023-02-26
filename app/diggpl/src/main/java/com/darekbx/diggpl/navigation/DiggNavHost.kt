package com.darekbx.diggpl.navigation

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
import com.darekbx.diggpl.ui.saved.SavedItemsScreen
import com.darekbx.diggpl.ui.stream.StreamScreen
import com.darekbx.diggpl.ui.tags.SavedTagsScreen
import com.darekbx.diggpl.ui.tags.TagsListScreen

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
            SavedTagsScreen(
                openTagsList = { navController.navigate(TagList.route) },
                openTag = { tag ->
                    navController.navigate("${TagStream.route}?${TagStream.tagArg}=$tag")
                }
            )
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

        composable(route = TagList.route) {
            TagsListScreen()
        }

        composable(
            route = TagStream.routeWithArgs,
            arguments = TagStream.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getString(TagStream.tagArg)?.let { tag ->
                StreamScreen(tag) { streamItem ->
                    val id = streamItem.id
                    when (streamItem.resource) {
                        ResourceType.ENTRY.type -> navController.navigate("${Entry.route}?${Entry.entryIdArg}=$id")
                        ResourceType.LINK.type -> navController.navigate("${Link.route}?${Link.linkIdArg}=$id")
                    }
                }
            }
        }

        composable(route = SavedItems.route) {
            SavedItemsScreen(
                openLink = { linkId -> navController.navigate("${Link.route}?${Link.linkIdArg}=$linkId") },
                openEntry = { entryId -> navController.navigate("${Entry.route}?${Entry.entryIdArg}=$entryId") }
            )
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
