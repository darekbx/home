package com.darekbx.timeline.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.timeline.ui.categories.CategoriesScreen
import com.darekbx.timeline.ui.home.HomeScreen

@Composable
fun TimelineNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home.route,
        modifier = modifier
    ) {
        composable(route = Home.route) {
            HomeScreen(
                onCategoriesClick = { navController.navigate(Categories.route) }
            )
        }

        composable(route = Categories.route) {
            CategoriesScreen()
        }
    }
}
