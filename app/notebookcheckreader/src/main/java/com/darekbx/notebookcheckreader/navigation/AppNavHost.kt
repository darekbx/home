package com.darekbx.notebookcheckreader.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.notebookcheckreader.ui.favourites.Favourites
import com.darekbx.notebookcheckreader.ui.news.News

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NewsDestination.route,
        modifier = modifier
    ) {
        composable(route = NewsDestination.route) {
            News()
        }

        composable(route = ToReadDestination.route) {
            Favourites()
        }
        
        composable(route = FavouritesDestination.route) {
            Favourites()
        }
    }
}
