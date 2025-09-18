package com.darekbx.emailbot.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.emailbot.ui.configuration.ui.ConfigurationScreen
import com.darekbx.emailbot.ui.emails.EmailsScreen
import com.darekbx.emailbot.ui.filters.FiltersScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ConfigurationDestination.route,
        modifier = modifier
    ) {
        composable(route = ConfigurationDestination.route) {
            ConfigurationScreen(onConfigurationSaved = {
                navController.navigate(EmailsDestination.route) {
                    popUpTo(ConfigurationDestination.route) { inclusive = true }
                }
            })
        }

        composable(route = EmailsDestination.route) {
            EmailsScreen()
        }

        composable(route = FiltersDestination.route) {
            FiltersScreen()
        }
    }
}
