package com.darekbx.tasks

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.LauncherActivity
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.tasks.navigation.TasksNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksActivity : LauncherActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeTheme(isDarkTheme = false) {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    TasksNavHost(navController)
                }
            }
        }
    }
}
