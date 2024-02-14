package com.darekbx.words

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.darekbx.common.LauncherActivity
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.words.ui.WordsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WordsActivity : LauncherActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeTheme(isDarkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    WordsScreen()
                }
            }
        }
    }
}
