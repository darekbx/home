package com.darekbx.weather.data

import com.darekbx.weather.data.network.antistorm.AntistormService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherRepository @Inject constructor(val antistormService: AntistormService) {

    fun test() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = antistormService.getPaths("radar")
            val c = data.size
        }
    }
}