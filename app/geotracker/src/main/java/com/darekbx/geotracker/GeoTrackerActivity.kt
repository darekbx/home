package com.darekbx.geotracker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.LauncherActivity
import com.darekbx.geotracker.domain.usecase.GetCountPlacesUseCase
import com.darekbx.geotracker.navigation.GeoTrackerNavHost
import com.darekbx.geotracker.navigation.HomeDestination
import com.darekbx.geotracker.navigation.MapDestination
import com.darekbx.geotracker.navigation.PlacesToVisitDestination
import com.darekbx.geotracker.navigation.SettingsDestination
import com.darekbx.geotracker.navigation.StatisticsDestination
import com.darekbx.geotracker.navigation.TripsDestination
import com.darekbx.geotracker.navigation.navigateSingleTopTo
import com.darekbx.geotracker.ui.theme.GeoTrackerTheme
import com.darekbx.geotracker.ui.theme.LocalColors
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * TODO:
 *  - add ability to edit track
 */

@AndroidEntryPoint
class GeoTrackerActivity : LauncherActivity() {

    @Inject
    lateinit var countPlacesUseCase: GetCountPlacesUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            GeoTrackerTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        content = { innerPadding ->
                            GeoTrackerNavHost(
                                modifier = Modifier.padding(
                                    innerPadding
                                ), navController = navController
                            )
                        },
                        bottomBar = { BottomMenu(navController, countPlacesUseCase) }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomMenu(navController: NavHostController, countPlacesUseCase: GetCountPlacesUseCase) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var placesCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        placesCount = countPlacesUseCase.invoke()
    }
    Surface(shadowElevation = 12.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuItem(
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController.navigateSingleTopTo(HomeDestination.route) },
                label = "Home",
                icon = painterResource(id = R.drawable.ic_home),
                selected = navBackStackEntry?.destination?.route == HomeDestination.route
            )
            MenuItem(
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController.navigateSingleTopTo(MapDestination.route) },
                label = "Map",
                icon = painterResource(id = R.drawable.ic_map),
                selected = navBackStackEntry?.destination?.route == MapDestination.route
            )
            MenuItem(
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController.navigateSingleTopTo(TripsDestination.route) },
                label = "Trips",
                icon = painterResource(id = R.drawable.ic_trips),
                selected = navBackStackEntry?.destination?.route == TripsDestination.route
            )
            MenuItem(
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController.navigateSingleTopTo(PlacesToVisitDestination.route) },
                label = "To visit",
                icon = painterResource(id = R.drawable.ic_to_visit),
                selected = navBackStackEntry?.destination?.route == PlacesToVisitDestination.route,
                count = placesCount
            )
            MenuItem(
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController.navigateSingleTopTo(StatisticsDestination.route) },
                label = "Statistics",
                icon = painterResource(id = R.drawable.ic_statistics),
                selected = navBackStackEntry?.destination?.route == StatisticsDestination.route
            )
            MenuItem(
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController.navigateSingleTopTo(SettingsDestination.route) },
                label = "Settings",
                icon = painterResource(id = R.drawable.ic_settings),
                selected = navBackStackEntry?.destination?.route == SettingsDestination.route
            )
        }
    }
}

@Composable
fun MenuItem(
    modifier: Modifier = Modifier,
    label: String,
    icon: Painter,
    selected: Boolean,
    count: Int = 0
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
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
                fontSize = 10.sp
            )
        }
        if (count > 0) {
            Text(
                text = "$count",
                modifier = Modifier
                    .padding(bottom = 32.dp, start = 16.dp)
                    .size(18.dp)
                    .background(
                        LocalColors.current.red,
                        CircleShape
                    ).padding(top = 1.dp, start = 1.dp),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.W700,
            )
        }
    }
}

@Preview
@Composable
fun SelectedMenuItemPreview() {
    MenuItem(
        label = "Selected",
        icon = painterResource(id = R.drawable.ic_settings),
        selected = true
    )
}

@Preview
@Composable
fun MenuItemPreview() {
    MenuItem(
        label = "Default",
        icon = painterResource(id = R.drawable.ic_settings),
        selected = false
    )
}
