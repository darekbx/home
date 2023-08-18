package com.darekbx.storage.legacy

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getFloatOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.darekbx.storage.BuildConfig
import com.darekbx.storage.legacy.model.LegacyPlace
import com.darekbx.storage.legacy.model.LegacyPoint
import com.darekbx.storage.legacy.model.LegacyRoute
import com.darekbx.storage.legacy.model.LegacyTrack
import java.io.File
import java.io.FileOutputStream

class GeoTrackerHelper(private val context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, 1) {

    private var database: SQLiteDatabase

    companion object {
        const val DB_NAME = "geotracker_db.sqlite"
    }

    init {
        if (BuildConfig.DEBUG || !context.databaseList().contains(DB_NAME)) {
            copyLegacyDatabase()
        }

        database = SQLiteDatabase.openDatabase(
            context.getDatabasePath(DB_NAME),
            SQLiteDatabase.OpenParams.Builder().addOpenFlags(SQLiteDatabase.OPEN_READONLY).build()
        )
    }

    override fun onCreate(db: SQLiteDatabase?) {}

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    fun getPlaces(): List<LegacyPlace> {
        val items = mutableListOf<LegacyPlace>()
        database.rawQuery("SELECT * FROM place", emptyArray())?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    items.add(
                        LegacyPlace(
                            cursor.getString(1),
                            cursor.getDouble(2),
                            cursor.getDouble(3),
                            cursor.getLong(4)
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        return items
    }

    fun getPoints(): List<LegacyPoint> {
        val items = mutableListOf<LegacyPoint>()
        database.rawQuery("SELECT * FROM point", emptyArray())?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    items.add(
                        LegacyPoint(
                            cursor.getLong(1),
                            cursor.getLong(2),
                            cursor.getDouble(3),
                            cursor.getDouble(4),
                            cursor.getFloat(5),
                            cursor.getDouble(6)
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        return items
    }

    fun getRoutes(): List<LegacyRoute> {
        val items = mutableListOf<LegacyRoute>()
        database.rawQuery("SELECT * FROM route", emptyArray())?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    items.add(
                        LegacyRoute(
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getLong(3)
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        return items
    }

    fun getTracks(): List<LegacyTrack> {
        val items = mutableListOf<LegacyTrack>()
        database.rawQuery("SELECT * FROM track", emptyArray())?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    items.add(
                        LegacyTrack(
                            cursor.getStringOrNull(1),
                            cursor.getLong(2),
                            cursor.getLongOrNull(3),
                            cursor.getFloatOrNull(4),
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        return items
    }

    private fun copyLegacyDatabase() {
        val firstDb = context.databaseList().first()
        val dbDir = context.getDatabasePath(firstDb).parentFile
        context.assets.open(DB_NAME).use { inputStream ->
            FileOutputStream(File(dbDir, DB_NAME)).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}