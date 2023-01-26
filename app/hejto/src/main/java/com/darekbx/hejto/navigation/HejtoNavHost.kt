package com.darekbx.hejto.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.hejto.ui.posts.PostScreen
import com.darekbx.hejto.ui.posts.PostsScreen

@Composable
fun HejtoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Board.route,
        modifier = modifier
    ) {
        composable(route = Board.route) {
            PostsScreen { postSlug ->
                navController.navigateSingleTopTo("${Post.route}?${Post.slugArg}=$postSlug")
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