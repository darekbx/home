package com.darekbx.timeline

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.LauncherActivity
import com.darekbx.timeline.navigation.TimelineNavHost
import com.darekbx.timeline.ui.theme.TimelineTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * TODO:
 *  - display list of entries with ability to remove them
 */
@AndroidEntryPoint
class TimelineActivity : LauncherActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimelineTheme {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    TimelineNavHost(navController)
                }
            }
        }
    }
}