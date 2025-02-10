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
import com.darekbx.vault.data.VaultRepository
import com.darekbx.vault.navigation.*
import com.darekbx.vault.ui.PinScreen as LegacyPinScreen
import com.darekbx.vault.ui.ListScreen as LegacyListScreen
import com.darekbx.vault.ui.ItemScreen as LegacyItemScreen
import com.darekbx.vault2.ui.PinScreen as PinScreen
import com.darekbx.vault2.ui.ListScreen as ListScreen
import com.darekbx.vault2.ui.ItemScreen as ItemScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VaultActivity : LauncherActivity() {

    @Inject
    lateinit var vaultRepository: VaultRepository

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            navController = rememberNavController()
            HomeTheme(isDarkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Navigation(navController!!)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        vaultRepository.reset()
        clearBackStack()
    }

    private fun clearBackStack() {
        navController?.navigate(RoutePin.route) {
            popUpTo(navController!!.graph.id) {
                inclusive = true
            }
        }
    }

    @Composable
    private fun Navigation(navController: NavHostController) {
        if (USES_NEW_VAULT) {
            vault(navController)
        } else {
            legacyVault(navController)
        }
    }

    @Composable
    private fun vault(navController: NavHostController) {
        NavHost(navController, startDestination = RoutePin.route) {
            composable(RoutePin.route) {
                PinScreen(onSuccess = { navController.navigateSingleTopTo(RouteList.route) })
            }
            composable(RouteList.route) {
                ListScreen(onItemClick = { secretId ->
                    with (RouteSecret) {
                        navController.navigate("$route?$secretIdArg=$secretId")
                    }
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

    @Composable
    private fun legacyVault(navController: NavHostController) {
        NavHost(navController, startDestination = RoutePin.route) {
            composable(RoutePin.route) {
                LegacyPinScreen(onSuccess = { navController.navigateSingleTopTo(RouteList.route) })
            }
            composable(RouteList.route) {
                LegacyListScreen(onItemClick = { secretId ->
                    with (RouteSecret) {
                        navController.navigate("$route?$secretIdArg=$secretId")
                    }
                })
            }
            composable(
                route = RouteSecret.routeWithArgs,
                arguments = RouteSecret.arguments
            ) { navBackStackEntry ->
                navBackStackEntry.arguments?.getLong(RouteSecret.secretIdArg)?.let { secretId ->
                    LegacyItemScreen(id = secretId)
                }
            }
        }
    }

    companion object {
        private const val USES_NEW_VAULT = false
    }
}
