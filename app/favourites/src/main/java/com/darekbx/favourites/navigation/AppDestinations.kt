package com.darekbx.favourites.navigation

interface AppDestination {
    val route: String
}

object ProductsDestination : AppDestination {
    override val route = "products"
}
