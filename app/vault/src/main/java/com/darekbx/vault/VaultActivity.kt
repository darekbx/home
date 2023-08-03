package com.darekbx.vault

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.LauncherActivity
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.vault.navigation.*
import com.darekbx.vault.ui.ItemScreen
import com.darekbx.vault.ui.ListScreen
import com.darekbx.vault.ui.PinScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VaultActivity : LauncherActivity() {

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
        NavHost(navController, startDestination = RoutePin.route) {
            composable(RoutePin.route) {
                PinScreen(onSuccess = { navController.navigateSingleTopTo(RouteList.route) })
            }
            composable(RouteList.route) {
                ListScreen(onItemClick = { secretId ->
                    navController.navigate("${RouteSecret.route}?${RouteSecret.secretIdArg}=$secretId")
                })
            }
            composable(
                route = RouteSecret.routeWithArgs,
                arguments = RouteSecret.arguments
            ) { navBackStackEntry ->
                navBackStackEntry.arguments?.getLong(RouteSecret.secretIdArg)?.let { secretId ->
                    ItemScreen(id = secretId)
                }
            }
        }
    }

}
