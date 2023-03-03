package com.darekbx.fuel

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
import com.darekbx.fuel.navigation.NavigationItem
import com.darekbx.fuel.ui.chart.ChartScreen
import com.darekbx.fuel.ui.list.FuelEntriesScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FuelActivity : ComponentActivity() {

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
        NavHost(navController, startDestination = NavigationItem.EntriesList.route) {
            composable(NavigationItem.EntriesList.route) {
                FuelEntriesScreen {
                    navController.navigate(NavigationItem.EntriesChart.route)
                }
            }
            composable(NavigationItem.EntriesChart.route) {
                ChartScreen()
            }
        }
    }
}
