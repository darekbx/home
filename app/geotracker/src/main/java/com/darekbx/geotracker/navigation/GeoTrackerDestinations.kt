package com.darekbx.geotracker.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface GeoTrackerDestinations {
    val route: String
}

object HomeDestination : GeoTrackerDestinations {
    override val route = "home"
}

object MapDestination : GeoTrackerDestinations {
    override val route = "map"
}

object TripsDestination : GeoTrackerDestinations {
    override val route = "trips"
}

object StatisticsDestination : GeoTrackerDestinations {
    override val route = "statistics"
}

object TripDestination : GeoTrackerDestinations {
    override val route = "trip"
    const val tripIdArg = "trip_id"
    val routeWithArgs = "${route}?$tripIdArg={${tripIdArg}}"
    val arguments = listOf(
        navArgument(tripIdArg) {
            nullable = false
            defaultValue = 0
            type = NavType.LongType
        }
    )
}

object PlacesToVisitDestination : GeoTrackerDestinations {
    override val route = "places_to_visit"
}

object CalendarDestination : GeoTrackerDestinations {
    override val route = "calendar"
}

object SettingsDestination : GeoTrackerDestinations {
    override val route = "settings"
}
