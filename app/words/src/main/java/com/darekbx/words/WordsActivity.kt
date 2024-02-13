package com.darekbx.words

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.darekbx.common.LauncherActivity
import com.darekbx.common.ui.theme.HomeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WordsActivity : LauncherActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeTheme(isDarkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize()) {

                }
            }
        }
    }
}
