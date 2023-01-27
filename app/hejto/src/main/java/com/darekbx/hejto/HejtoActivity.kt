@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.hejto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.ui.NoInternetView
import com.darekbx.common.utils.ConnectionUtils
import com.darekbx.hejto.navigation.*
import com.darekbx.hejto.ui.HejtoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Screens:
 * - MainScreen
 *  - Display posts
 *      - With filter on top
 *          - period: (all|6h|12h|24h|week|month|year)
 *          - type: (discussion|link|offer|article)
 *      - Filter is persisted between sessions
 *  - Menu:
 *      - Communities
 *      - Tags
 *      - Settings
 * - Communities (favourite communities)
 *   - list of the communities like in settings but with new posts counter
 * - Community
 *   - list of community posts with comment count and others
 *   - each entry can be opened to view details and comments
 * - Tags
 *   - list of the tags with new posts counter
 * - Tag
 *   - list of tag posts with comment count and others
 *   - each entry can be opened to view details and comments
 *
 * - Settings
 *  - Communities (list with communities, and filtering, ability to mark favourite communities)
 *  - Tags (list with tags, and filtering, ability to mark favourite tags)
 */
@AndroidEntryPoint
class HejtoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            HejtoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (ConnectionUtils.isInternetConnected(LocalContext.current)) {
                        Scaffold(
                            content = { innerPadding ->
                                HejtoNavHost(
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

    @Composable
    private fun BottomMenu(navController: NavHostController) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuItem(
                modifier = Modifier.clickable { navController.navigateSingleTopTo("${FavouriteTags.route}") },
                label = "Tags",
                icon = painterResource(id = R.drawable.ic_label)
            )
            MenuItem(
                modifier = Modifier.clickable { navController.navigateSingleTopTo("${Communities.route}") },
                label = "Communities",
                icon = painterResource(id = R.drawable.ic_communities)
            )
            MenuItem(
                modifier = Modifier.clickable { navController.navigateSingleTopTo("${Settings.route}") },
                label = "Settings",
                icon = painterResource(id = R.drawable.ic_settings)
            )
        }
    }
}

@Composable
private fun MenuItem(modifier: Modifier = Modifier, label: String, icon: Painter) {
    Column(
        modifier = modifier,
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
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.W200
        )
    }
}
