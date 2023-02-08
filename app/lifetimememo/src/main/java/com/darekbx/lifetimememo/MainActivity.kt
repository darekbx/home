@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.lifetimememo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darekbx.lifetimememo.navigation.*
import com.darekbx.lifetimememo.commonui.MemosBottomNavigation
import com.darekbx.lifetimememo.commonui.theme.LifeTimeMemoTheme
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration

/**
 * Life Time Memo
 *
 * Mobile & Tablet
 *  - biometric security to enter, like alior, ask for fingerprint on the first screen
 *  - ability to search by title, short info, description, categories, flags, with location
 *  - encrypted db?
 *  - tree view is displaying node name and year (or month if timestamp < one year)
 *  - single memo can display a map in a dialog if localization is conncted
 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            applicationContext.getSharedPreferences("maps", MODE_PRIVATE)
        )

        setContent {
            LifeTimeMemoTheme {
                MemoApp()
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun MemoApp() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    Scaffold(
        bottomBar = {
            MemosBottomNavigation(
                allScreens = appTabRowScreens,
                onTabSelected = { newScreen ->
                    navController.navigateSingleTopTo(newScreen.route)
                },
                currentScreen = obtainCurrentScreen(currentDestination?.route)
            )
        }
    ) { innerPadding ->
        MemoNavHost(modifier = Modifier.padding(innerPadding), navController = navController)
    }
}

private fun obtainCurrentScreen(currentDestination: String?): MemoDestination {
    val settingsDestinations = listOf(Settings, Categories)
    if (settingsDestinations.any { it.route == currentDestination }) {
        return Settings
    }
    return appTabRowScreens
        .find { it.route == currentDestination }
        ?: Memos
}