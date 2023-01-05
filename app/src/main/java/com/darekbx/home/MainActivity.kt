package com.darekbx.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.darekbx.common.ui.theme.HomeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = applicationContext

        setContent {
            HomeTheme(isDarkTheme = true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    Text(text = """
                        Idea: 
                            - dynamic theme colors (from wallpaper) https://betterprogramming.pub/materialyou-dynamic-colors-with-jetpack-compose-5a9bd00130e7
                            - there's no own launcher, use dafault one 
                            - airly is a widget 
                            - antistorm is a widget
                            - stocks are widgets on different screens
                            - all apps are from one project
                        TODO:
                            - Stocks: add real API requests, without DB caching
                            
                            """.trimIndent())
                    //ApplicationsList()
                }
            }
        }
    }
}

