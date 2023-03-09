package com.darekbx.rssreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.rssreader.ui.NewsList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RssReaderActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeTheme(isDarkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NewsList()
                }
            }
        }
    }
}
