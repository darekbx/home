package com.darekbx.spreadsheet.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.json.JSONObject

data class Style(
    val color: Int = Color.White.toArgb(),
    val bold: Boolean = false,
    val align: Align = Align.LEFT
) {

    enum class Align {
        LEFT, CENTER, RIGHT
    }

    fun composeColor() = Color(color)

    companion object {

        fun Style.toJson(): String {
            val jsonObject = JSONObject()
            jsonObject.put("color", this.color)
            jsonObject.put("bold", this.bold)
            jsonObject.put("align", this.align.name)
            return jsonObject.toString()
        }

        fun String.styleFromJson(): Style {
            if (this.isBlank()) {
                return Style()
            }
            val jsonObject = JSONObject(this)
            return Style(
                color = jsonObject.getInt("color"),
                bold = jsonObject.getBoolean("bold"),
                align = Align.valueOf(jsonObject.getString("align"))
            )
        }
    }
}
