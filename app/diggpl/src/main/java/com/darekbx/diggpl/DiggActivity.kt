@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.diggpl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.ui.NoInternetView
import com.darekbx.common.utils.ConnectionUtils
import com.darekbx.diggpl.navigation.*
import com.darekbx.diggpl.ui.DiggTheme
import com.darekbx.diggpl.ui.saved.SavedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiggActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            DiggTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (ConnectionUtils.isInternetConnected(LocalContext.current)) {
                        Scaffold(
                            content = { innerPadding ->
                                DiggNavHost(
                                    modifier = Modifier.padding(
                                        innerPadding
                                    ), navController = navController
                                )
                            },
                            bottomBar = { BottomMenu(navController) }
                        )
                    } else {
                        NoInternetView(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomMenu(
    navController: NavHostController,
    savedViewModel: SavedViewModel = hiltViewModel()
) {
    val savedCount by savedViewModel.countSavedItems().collectAsState(initial = 0)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuItem(
            modifier = Modifier.clickable { navController.navigateSingleTopTo(Homepage.route) },
            label = "Homepage",
            icon = painterResource(id = R.drawable.ic_home),
            selected = navBackStackEntry?.destination?.route == Homepage.route
        )
        MenuItem(
            modifier = Modifier.clickable { navController.navigateSingleTopTo(Tags.route) },
            label = "Tags",
            icon = painterResource(id = R.drawable.ic_label),
            selected = navBackStackEntry?.destination?.route == Tags.route
        )
        MenuItem(
            modifier = Modifier.clickable { navController.navigateSingleTopTo(SavedItems.route) },
            label = "  Saved  ",
            icon = painterResource(id = R.drawable.ic_save),
            selected = navBackStackEntry?.destination?.route == SavedItems.route,
            count = savedCount
        )
    }
}

@Composable
private fun MenuItem(
    modifier: Modifier = Modifier,
    label: String,
    icon: Painter,
    selected: Boolean,
    count: Int = 0
) {
    Box(modifier = Modifier, contentAlignment = Alignment.TopEnd) {
        Column(
            modifier = modifier.alpha(if (selected) 0.4F else 1F),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = icon,
                contentDescription = label,
                tint = Color.White
            )
            Text(
                text = label,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.W200,
            )
        }
        if (count > 0) {
            Text(
                text = "$count",
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        CircleShape
                    ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.W700,
            )
        }
    }
}
