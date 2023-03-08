package com.darekbx.tasks.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.tasks.ui.TaskListScreen
import com.darekbx.tasks.ui.TaskScreen

@Composable
fun TasksNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = TaskList.route,
        modifier = modifier
    ) {
        composable(route = TaskList.route) {
            TaskListScreen { taskId ->
                navController.navigate("${Task.route}?${Task.taskIdArg}=$taskId")

            }
        }

        composable(
            route = Task.routeWithArgs,
            arguments = Task.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getInt(Task.taskIdArg)?.let { taskId ->
                TaskScreen(taskId = taskId)
            }
        }
    }
}
