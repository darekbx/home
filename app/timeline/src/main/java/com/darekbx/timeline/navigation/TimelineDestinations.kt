package com.darekbx.timeline.navigation

interface TimelineDestinations {
    val route: String
}

object Home : TimelineDestinations {
    override val route = "home"
}

object Categories : TimelineDestinations {
    override val route = "categories"
}

object NewTimeline : TimelineDestinations {
    override val route = "new_timeline"
}
