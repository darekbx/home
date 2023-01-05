package com.darekbx.weather

import android.content.res.AssetManager
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.weather.data.WeatherRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WeatherActivity : ComponentActivity() {

    @Inject
    lateinit var weatherRepository: WeatherRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    weatherRepository.test()

                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .size(300.dp, 300.dp)
                    ) {

                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://antistorm.eu/visualPhenom/20230105.1428-radar-visualPhenomenon.png")
                                    .size(600, 600)
                                    .build()
                            ),
                            contentDescription = ""
                        )
                        Image(
                            modifier = Modifier.fillMaxSize().alpha(0.05F),
                            painter = rememberAsyncImagePainter(model = ImageRequest.Builder(LocalContext.current)
                                .data("https://antistorm.eu/archive/2023.1.5/5-30-radar-probabilitiesImg.png")
                                .size(600, 600)
                                .build()),
                            contentDescription = ""
                        )
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://antistorm.eu/visualPhenom/20230105.1438-storm-visualPhenomenon.png")
                                    .size(600, 600)
                                    .build()),
                            contentDescription = ""
                        )
                    }
                }
            }
        }
    }
}