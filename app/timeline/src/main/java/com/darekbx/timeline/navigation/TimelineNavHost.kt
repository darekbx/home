package com.darekbx.timeline.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.timeline.ui.categories.CategoriesScreen
import com.darekbx.timeline.ui.home.HomeScreen
import com.darekbx.timeline.ui.newtimeline.NewTimelineScreen

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
                onCategoriesClick = { navController.navigate(Categories.route) },
                onNewTimelineClick = { navController.navigate(NewTimeline.route) }
            )
        }

        composable(route = Categories.route) {
            CategoriesScreen()
        }

        composable(route = NewTimeline.route) {
            NewTimelineScreen()
        }
    }
}