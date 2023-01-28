package com.darekbx.hejto.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.hejto.ui.communities.CommunitesScreen
import com.darekbx.hejto.ui.posts.PostScreen
import com.darekbx.hejto.ui.posts.PostsScreen
import com.darekbx.hejto.ui.settings.SettingsScreen
import com.darekbx.hejto.ui.tags.FavouriteTagsScreen
import com.darekbx.hejto.ui.tags.TagsListScreen

@Composable
fun HejtoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = FavouriteTags.route,
        modifier = modifier
    ) {
        composable(route = Board.route) {
            PostsScreen { postSlug ->
                navController.navigateSingleTopTo("${Post.route}?${Post.slugArg}=$postSlug")
            }
        }

        composable(
            route = BoardByTag.routeWithArgs,
            arguments = BoardByTag.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getString(BoardByTag.tagArg)?.let { tag ->
                PostsScreen(tag = tag) { postSlug ->
                    navController.navigate("${Post.route}?${Post.slugArg}=$postSlug")
                }
            }
        }

        composable(
            route = Post.routeWithArgs,
            arguments = Post.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getString(Post.slugArg)?.let { postSlug ->
                PostScreen(postSlug)
            }
        }

        composable(route = FavouriteTags.route) {
            FavouriteTagsScreen(
                openTagsList = { navController.navigateSingleTopTo(TagList.route) },
                openTag = { tag ->
                    navController.navigate("${BoardByTag.route}?${BoardByTag.tagArg}=$tag")
                }
            )
        }

        composable(route = TagList.route) {
            TagsListScreen()
        }

        composable(route = Settings.route) {
            SettingsScreen()
        }

        composable(route = Communities.route) {
            CommunitesScreen()
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