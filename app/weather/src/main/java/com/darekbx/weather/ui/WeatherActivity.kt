package com.darekbx.weather.ui

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.weather.R
import com.darekbx.weather.ui.weather.WeatherView
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    WeatherView()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
    }
}