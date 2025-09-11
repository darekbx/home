package com.darekbx.spreadsheet.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darekbx.spreadsheet.ui.grid.SpreadsheetGrid
import com.darekbx.spreadsheet.ui.spreadsheet.SpreadSheets

@Composable
fun SpreadsheetNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = RouteSheets.route,
        modifier = modifier.fillMaxSize()
    ) {
        composable(RouteSheets.route) {
            SpreadSheets(
                openItem = { sheet ->
                    navController.navigate(RouteSheet.build(sheet.uid))
                }
            )
        }
        composable(
            route = RouteSheet.routeWithArgs,
            arguments = RouteSheet.arguments
        ) { backStackEntry ->
            val uid = RouteSheet.parse(backStackEntry.arguments!!)
            SpreadsheetGrid(spreadSheetUid = uid!!)
        }
    }
}