package com.darekbx.weather.data.network.rainviewer

class WeatherMap(val radar: Radar, val host: String, val version: String)

class Radar(val nowcast: Array<Nowcast>)

class Nowcast(val time: Long, val path: String)
