package com.darekbx.books

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darekbx.books.navigation.NavigationItem
import com.darekbx.books.navigation.navigateSingleTopTo
import com.darekbx.books.ui.LocalColors
import com.darekbx.books.ui.list.ListScreen
import com.darekbx.books.ui.statistics.StatisticsScreen
import com.darekbx.books.ui.toread.ToReadScreen
import com.darekbx.common.LauncherActivity
import com.darekbx.common.ui.theme.HomeTheme
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class BooksActivity : LauncherActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeTheme(isDarkTheme = false) {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        content = { innerPadding ->
                            Navigation(
                                modifier = Modifier.padding(
                                    innerPadding
                                ), navController = navController
                            )
                        },
                        bottomBar = { BottomMenu(navController) }
                    )
                }
            }
        }
    }

    @Composable
    private fun Navigation(modifier: Modifier, navController: NavHostController) {
        NavHost(navController, startDestination = NavigationItem.List.route, modifier = modifier) {
            composable(NavigationItem.List.route) {
                ListScreen()
            }
            composable(NavigationItem.ToRead.route) {
                ToReadScreen()
            }
            composable(NavigationItem.Statistics.route) {
                StatisticsScreen()
            }
        }
    }

    @Composable
    private fun BottomMenu(navController: NavHostController) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        Surface(shadowElevation = 12.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MenuItem(
                    modifier = Modifier.clickable { navController.navigateSingleTopTo(NavigationItem.List.route) },
                    label = "Books",
                    icon = painterResource(id = R.drawable.ic_book),
                    selected = navBackStackEntry?.destination?.route == NavigationItem.List.route
                )
                MenuItem(
                    modifier = Modifier.clickable { navController.navigateSingleTopTo(NavigationItem.ToRead.route) },
                    label = "To Read",
                    icon = painterResource(id = R.drawable.ic_to_read),
                    selected = navBackStackEntry?.destination?.route == NavigationItem.ToRead.route
                )
                MenuItem(
                    modifier = Modifier.clickable { navController.navigateSingleTopTo(NavigationItem.Statistics.route) },
                    label = "Statistics",
                    icon = painterResource(id = R.drawable.ic_statistics),
                    selected = navBackStackEntry?.destination?.route == NavigationItem.Statistics.route,
                )
            }
        }
    }

    @Composable
    private fun MenuItem(
        modifier: Modifier = Modifier,
        label: String,
        icon: Painter,
        selected: Boolean
    ) {
        Box(modifier = Modifier, contentAlignment = Alignment.TopEnd) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = icon,
                    contentDescription = label,
                    tint = if (selected) LocalColors.current.orange else Color.Gray
                )
                Text(
                    text = label,
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (selected) LocalColors.current.orange else Color.Gray,
                    fontWeight = FontWeight.W200,
                )
            }
        }
    }
}
