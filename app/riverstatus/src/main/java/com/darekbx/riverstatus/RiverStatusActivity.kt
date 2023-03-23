package com.darekbx.riverstatus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.riverstatus.waterlevel.ui.WaterlevelScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RiverStatusActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeTheme(isDarkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WaterlevelScreen(stationId = 152210170)
                }
            }
        }
    }
}
