package com.darekbx.weather

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.weather.navigation.NavigationItem
import com.darekbx.weather.ui.settings.SettingsScreen
import com.darekbx.weather.ui.weather.WeatherScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherActivity : ComponentActivity() {

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Approximate location access granted
            }
            else -> {
                Toast.makeText(this, R.string.no_location_permission, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeTheme {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    Navigation(navController)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
    }
}

@Composable
private fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Weather.route) {
        composable(NavigationItem.Weather.route) {
            WeatherScreen {
                navController.navigate(NavigationItem.Settings.route)
            }
        }
        composable(NavigationItem.Settings.route) {
            SettingsScreen()
        }
    }
}