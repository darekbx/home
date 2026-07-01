package com.darekbx.infopigula.navigation


interface AppDestination {
    val route: String
}

object HomeDestination : AppDestination {
    override val route = "home"
}