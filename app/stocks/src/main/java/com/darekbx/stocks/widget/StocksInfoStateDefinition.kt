package com.darekbx.stocks.widget

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object StocksInfoStateDefinition : GlanceStateDefinition<StocksInfo> {

    private const val DATA_STORE_FILENAME = "stocksInfo"

    private val Context.datastore by dataStore(DATA_STORE_FILENAME, StocksInfoSerializer)

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<StocksInfo> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }

    object StocksInfoSerializer : Serializer<StocksInfo> {
        override val defaultValue = StocksInfo.Unavailable("No data found")

        override suspend fun readFrom(input: InputStream): StocksInfo = try {
            Json.decodeFromString(
                StocksInfo.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Could not read stocks data: ${exception.message}")
        }

        override suspend fun writeTo(t: StocksInfo, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(StocksInfo.serializer(), t).encodeToByteArray()
                )
            }
        }
    }
}