package com.darekbx.emailbot.navigation

interface AppDestinations {
    val route: String
}

object ConfigurationDestination : AppDestinations {
    override val route = "configuration"
}

object EmailsDestination : AppDestinations {
    override val route = "emails"
}

object FiltersDestination : AppDestinations {
    override val route = "filters"
}

