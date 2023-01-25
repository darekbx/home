package com.darekbx.stocks

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.ui.NoInternetView
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.common.utils.ConnectionUtils
import com.darekbx.stocks.navigation.NavigationItem
import com.darekbx.stocks.ui.settings.SettingsScreen
import com.darekbx.stocks.ui.stocks.StocksScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StocksActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeTheme(isDarkTheme = true) {
                if (ConnectionUtils.isInternetConnected(LocalContext.current)) {
                    val navController = rememberNavController()
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Navigation(navController)
                    }
                } else {
                    NoInternetView(Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Stocks.route) {
        composable(NavigationItem.Stocks.route) {
            StocksScreen(openSettings = {
                navController.navigate(NavigationItem.Settings.route)
            })
        }
        composable(NavigationItem.Settings.route) {
            SettingsScreen()
        }
    }
}
