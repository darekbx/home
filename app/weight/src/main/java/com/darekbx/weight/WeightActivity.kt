package com.darekbx.weight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.weight.navigation.NavigationItem
import com.darekbx.weight.ui.chart.ChartScreen
import com.darekbx.weight.ui.list.ListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeightActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeTheme(isDarkTheme = false) {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    Navigation(navController)
                }
            }
        }
    }

    @Composable
    private fun Navigation(navController: NavHostController) {
        NavHost(navController, startDestination = NavigationItem.EntriesChart.route) {
            composable(NavigationItem.EntriesChart.route) {
                ChartScreen {
                    navController.navigate(NavigationItem.EntriesList.route)
                }
            }
            composable(NavigationItem.EntriesList.route) {
                ListScreen()
            }
        }
    }
}