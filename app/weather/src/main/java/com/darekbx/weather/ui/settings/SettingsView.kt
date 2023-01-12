@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.weather.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.weather.ui.weather.WeatherViewModel
import com.darekbx.weather.ui.weather.WeatherViewModel.Companion.DEFAULT_MAX_DISTANCE
import com.darekbx.weather.ui.weather.WeatherViewModel.Companion.DEFAULT_MAX_RESULTS

@Preview(showSystemUi = true)
@Composable
fun SettingsScreen(weatherViewModel: WeatherViewModel = hiltViewModel()) {
    val maxDistance by weatherViewModel.maxDistance.collectAsState(initial = DEFAULT_MAX_DISTANCE)
    val maxResults by weatherViewModel.maxResults.collectAsState(initial = DEFAULT_MAX_RESULTS)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Air quality settings", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = "${maxDistance}",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                if (it.isNotEmpty()) {
                    weatherViewModel.saveMaxDistance(it.toDouble())
                }
            },
            label = { Text(text = "Air quality max distance") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = "${maxResults}",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                if (it.isNotEmpty()) {
                    weatherViewModel.saveMaxResults(it.toInt())
                }
            },
            label = { Text(text = "Air quality max results") }
        )
    }
}