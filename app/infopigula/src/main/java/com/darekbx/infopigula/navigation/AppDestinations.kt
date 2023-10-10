package com.darekbx.infopigula.navigation


interface AppDestination {
    val route: String
}

object HomeDestination : AppDestination {
    override val route = "home"
}

object SettingsDestination : AppDestination {
    override val route = "settings"
}

object LoginDestination : AppDestination {
    override val route: String = "login"
}

object CreatorsDestination : AppDestination {
    override val route: String = "creators"
}