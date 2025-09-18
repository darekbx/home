package com.darekbx.notebookcheckreader.navigation

interface AppDestinations {
    val route: String
}

object NewsDestination : AppDestinations {
    override val route = "news_list"
}

object ToReadDestination : AppDestinations {
    override val route = "to_read"
}

object FavouritesDestination : AppDestinations {
    override val route = "favourites"
}
