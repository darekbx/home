package com.darekbx.geotracker.navigation

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

object PlacesToVisitDestination : GeoTrackerDestinations {
    override val route = "places_to_visit"
}

object CalendarDestination : GeoTrackerDestinations {
    override val route = "calendar"
}

object SettingsDestination : GeoTrackerDestinations {
    override val route = "settings"
}
