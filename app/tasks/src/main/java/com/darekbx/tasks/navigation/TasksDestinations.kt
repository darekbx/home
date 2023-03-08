package com.darekbx.tasks.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface TasksDestination {
    val route: String
}

object TaskList : TasksDestination {
    override val route = "task_list"
}

object Task : TasksDestination {
    override val route = "task"
    const val taskIdArg = "task_id"
    val routeWithArgs = "${route}?$taskIdArg={${taskIdArg}}"
    val arguments = listOf(
        navArgument(taskIdArg) {
            nullable = false
            defaultValue = 0
            type = NavType.IntType
        }
    )
}
