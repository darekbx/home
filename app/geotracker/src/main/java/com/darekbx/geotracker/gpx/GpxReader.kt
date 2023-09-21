package com.darekbx.geotracker.gpx

import android.location.Location
import com.darekbx.geotracker.repository.model.Point
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

class GpxReader {

    fun readGpx(inputStream: InputStream): Gpx {
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(inputStream, "UTF-8")

        var eventType = parser.eventType
        var currentTag = ""
        var name = ""

        val points = mutableListOf<Point>()

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    currentTag = parser.name
                    when (currentTag) {
                        "trkpt" -> {
                            val lat = parser.getAttributeValue(null, "lat")
                            val lon = parser.getAttributeValue(null, "lon")
                            if (lat != null && lon != null) {
                                points.add(Point(lat.toDouble(), lon.toDouble()))
                            }
                        }
                    }
                }

                XmlPullParser.TEXT -> {
                    when (currentTag) {
                        "name" -> {
                            if (parser.text?.trim()?.isNotBlank() == true) {
                                name = parser.text
                            }
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        val distance = calculateDistance(points)
        return Gpx(name, points, distance)
    }

    private fun calculateDistance(points: MutableList<Point>): Double {
        var distance = 0.0
        val out = FloatArray(1)

        for (i in 1..(points.size - 1)) {
            Location.distanceBetween(
                points[i - 1].latitude, points[i - 1].longitude,
                points[i].latitude, points[i].longitude,
                out
            )
            distance += out[0].toDouble()
        }

        return distance
    }
}
