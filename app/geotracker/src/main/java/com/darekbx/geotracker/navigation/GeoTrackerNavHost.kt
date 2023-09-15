package com.darekbx.geotracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.geotracker.ui.home.HomeScreen
import com.darekbx.geotracker.ui.trip.TripScreen
import com.darekbx.geotracker.ui.trips.TripsScreen

@Composable
fun GeoTrackerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen()
        }

        composable(route = TripsDestination.route) {
            TripsScreen { track ->
                navController.navigate("${TripDestination.route}?${TripDestination.tripIdArg}=${track.id}")
            }
        }
        composable(
            route = TripDestination.routeWithArgs,
            arguments = TripDestination.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getLong(TripDestination.tripIdArg)?.let { tripId ->
                TripScreen(trackId = tripId)
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
