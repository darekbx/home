package com.darekbx.storage.legacy

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.darekbx.storage.BuildConfig
import com.darekbx.storage.legacy.model.LegacyDot
import java.io.File
import java.io.FileOutputStream

class DotPadHelper(private val context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, 1) {

    private var database: SQLiteDatabase

    companion object {
        const val DB_NAME = "dotpad.sqlite"
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

    fun getDots(): List<LegacyDot> {
        val items = mutableListOf<LegacyDot>()
        database.rawQuery(
            "SELECT id, text, size, color, position_x, position_y, created_date, is_archived, is_sticked, reminder, calendar_event_id, calendar_reminder_id FROM dots",
            emptyArray()
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    items.add(
                        LegacyDot(
                            cursor.getLong(0),
                            cursor.getString(1),
                            cursor.getInt(2),
                            cursor.getInt(3),
                            cursor.getInt(4),
                            cursor.getInt(5),
                            cursor.getLong(6),
                            cursor.getInt(7) == 1,
                            cursor.getInt(8) == 1,
                            cursor.getLong(9),
                            cursor.getLong(10),
                            cursor.getLong(11),
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