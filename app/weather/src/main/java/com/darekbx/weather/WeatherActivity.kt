package com.darekbx.weather

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.weather.navigation.NavigationItem
import com.darekbx.weather.ui.settings.SettingsScreen
import com.darekbx.weather.ui.weather.WeatherScreen
import com.skydoves.cloudy.Cloudy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherActivity : ComponentActivity() {

    private val locationPermissionRequest = registerForActivityResult(
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
                //TutorialsBlurViewSample()

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

/**
 * Blur list elements content and highlight one item
 * + implementation "com.github.skydoves:cloudy:0.1.1"
 */
@Composable
private fun TutorialsBlurViewSample() {
    Box(modifier = Modifier.fillMaxSize()) {
        TutorialsView()

        // Display list on the bottom, to show that list will appear from the bottom
        ItemsList(
            modifier = Modifier
                .padding(top = 500.dp)
                .background(Color.White),
            items = (0..30).toList()
        )
    }
}

@Preview
@Composable
fun TutorialsView(modifier: Modifier = Modifier) {
    var parentSize by remember { mutableStateOf(IntSize.Zero) }
    val items = (0..30).toList()
    val blurRadiusSize = 15

    Box(modifier = modifier.onGloballyPositioned {
        parentSize = it.size
    }) {
        ItemsList(items = items)

        Cloudy(
            radius = blurRadiusSize,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75F))
        ) { }

        val indexVisible = 14

        ItemsList(items = items, itemVisible = indexVisible)
    }
}

@Composable
private fun ItemsList(modifier: Modifier = Modifier, items: List<Int>, itemVisible: Int = -1) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(96.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier
                    .alpha(if (itemVisible == -1 || itemVisible - 1 == item) 1F else 0F)
                    .padding(4.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
            ) {
                Text(
                    text = "#${item + 1}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
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
