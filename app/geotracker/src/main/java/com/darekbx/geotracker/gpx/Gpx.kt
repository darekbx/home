package com.darekbx.geotracker.gpx

import com.darekbx.geotracker.repository.model.Point

data class Gpx(val name: String, val points: List<Point>, val distance: Double)