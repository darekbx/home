package com.darekbx.hejto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.ui.NoInternetView
import com.darekbx.common.utils.ConnectionUtils
import com.darekbx.hejto.navigation.HejtoNavHost
import com.darekbx.hejto.ui.HejtoTheme
import com.darekbx.hejto.ui.posts.PostsScreen
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
                        HejtoNavHost(navController = navController)
                    } else {
                        NoInternetView(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}
