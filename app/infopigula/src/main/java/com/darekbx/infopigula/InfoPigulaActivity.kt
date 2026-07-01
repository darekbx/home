@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.infopigula

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.LauncherActivity
import com.darekbx.infopigula.navigation.AppNavHost
import com.darekbx.infopigula.ui.theme.InfoPigulaTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * TODO:
 * - settings screen
 *   - filtered groups
 */
@AndroidEntryPoint
class InfoPigulaActivity : LauncherActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InfoPigulaTheme(isDarkTheme = true) {
                MainContent()
            }
        }
    }
}

@Composable
private fun MainContent() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar() }
    ) { contentPadding ->
        AppNavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            navController = navController
        )
    }
}

@Composable
private fun TopAppBar() {
    TopAppBar(title = { Title() })
}

@Composable
private fun Title() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo), contentDescription = "logo",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
    }
}