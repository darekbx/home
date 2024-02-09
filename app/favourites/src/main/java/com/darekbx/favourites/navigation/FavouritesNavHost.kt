package com.darekbx.favourites.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.favourites.ui.products.ProductsScreen

@Composable
fun FavouritesNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ProductsDestination.route,
        modifier = modifier
    ) {
        composable(route = ProductsDestination.route) {
            ProductsScreen()
        }
    }
}
