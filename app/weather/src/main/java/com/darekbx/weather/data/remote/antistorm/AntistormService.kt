package com.darekbx.weather.data.remote.antistorm

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type

typealias Paths = Map<PathsType, List<String>>

interface AntistormService {

    @GET("/ajaxPaths.php?lastTimestamp=0")
    suspend fun getPaths(@Query("type") type: String): Paths

    companion object {
        const val ANTISTORM_BASE_URL = "https://antistorm.eu"
    }
}

enum class DirType(val label: String) {
    TYPE_RADAR("radar"),
    TYPE_STORM("storm")
}

enum class PathsType(val prefix: String) {
    DIRS_NAME("nazwa_folderu"),
    FILES_NAME("nazwa_pliku"),
    FILES_FRONT_NAME("nazwa_pliku_front")
}

class AntistormPathsConverter : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, Paths>? {
        return object : Converter<ResponseBody, Paths> {
            override fun convert(value: ResponseBody): Paths? {
                val lines = value.string().split("<br>")
                return PathsType.values().associate { type ->
                    type to extractItems(lines, type.prefix)
                }
            }
        }
    }

    private fun extractItems(lines: List<String>, prefix: String): List<String> {
        val line = lines.find { it.startsWith(prefix) } ?: return emptyList()
        return line.removePrefix("$prefix:").split(',')
    }
}