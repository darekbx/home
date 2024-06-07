package com.darekbx.geotracker.externaldisplay

import android.graphics.Bitmap
import android.graphics.Picture
import android.util.Log
import java.io.ByteArrayOutputStream

class ExternalDisplay {

    fun sendFrame(
        mapWidth: Int,
        mapHeight: Int,
        picture: Picture
    ) {
        val s2 = System.currentTimeMillis()

        // TODO can be optimized by using createScaledBitmap and scaling canvas
        // Draw contents into bitmap
        val bitmap = Bitmap.createBitmap(mapWidth, mapHeight, CONFIG)
        with(android.graphics.Canvas(bitmap)) {
            drawColor(android.graphics.Color.WHITE)
            drawPicture(picture)
        }

        // Scale bitmap
        val aspectRatio = (mapHeight / mapWidth.toFloat())
        val outHeight = (DISPLAY_WIDTH * aspectRatio).toInt()
        val scaled = Bitmap.createScaledBitmap(bitmap, DISPLAY_WIDTH, outHeight, false)

        // Crop bitmap to display size
        val offsetX = (scaled.width - DISPLAY_WIDTH) / 2
        val offsetY = (scaled.height - DISPLAY_HEIGHT) / 2
        val bitmapPart = Bitmap.createBitmap(scaled, offsetX, offsetY, DISPLAY_WIDTH, DISPLAY_HEIGHT)

        // Compress bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmapPart.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)

        // Send over bluetooth

        Log.v("sigma", "part2: ${System.currentTimeMillis() - s2}ms")
        Log.v("sigma", "byte size: ${byteArrayOutputStream.size() / 1024}kb")
    }

    companion object {
        // External display configuration
        private const val DISPLAY_WIDTH = 280
        private const val DISPLAY_HEIGHT = 240
        private val CONFIG = Bitmap.Config.RGB_565
    }
}