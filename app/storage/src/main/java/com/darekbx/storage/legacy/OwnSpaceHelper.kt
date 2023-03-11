package com.darekbx.storage.legacy

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.OPEN_READONLY
import android.database.sqlite.SQLiteDatabase.OpenParams
import android.database.sqlite.SQLiteOpenHelper
import com.darekbx.storage.legacy.model.LegacyTask
import com.darekbx.storage.legacy.model.LegacyWeightEntry
import java.io.File
import java.io.FileOutputStream

class OwnSpaceHelper(private val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1) {

    private var database: SQLiteDatabase

    companion object {
        const val DB_NAME = "own-space.db"
    }

    init {
        if (!context.databaseList().contains(DB_NAME)) {
            copyLegacyDatabase()
        }

        database = SQLiteDatabase.openDatabase(
            context.getDatabasePath(DB_NAME),
            OpenParams.Builder().addOpenFlags(OPEN_READONLY).build()
        )
    }

    override fun onCreate(db: SQLiteDatabase?) { }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }

    fun getTasks(): List<LegacyTask> {
        val tasks = mutableListOf<LegacyTask>()
        database.rawQuery("SELECT * FROM tasks", emptyArray())?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    tasks.add(LegacyTask(cursor.getString(1), cursor.getString(2), cursor.getString(3)))
                } while (cursor.moveToNext())
            }
        }
        return tasks
    }

    fun getNotes(): List<String> {
        val notes = mutableListOf<String>()
        database.rawQuery("SELECT * FROM notes ORDER BY `index`", emptyArray())?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    notes.add(cursor.getString(1))
                } while (cursor.moveToNext())
            }
        }
        return notes
    }

    fun getWeightEntries(): List<LegacyWeightEntry> {
        val items = mutableListOf<LegacyWeightEntry>()
        database.rawQuery("SELECT * FROM entries", emptyArray())?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    var date = cursor.getLong(1)
                    // Fix timestamp in seconds
                    if (date < 1600000000) {
                       date *= 1000
                    }
                    items.add(LegacyWeightEntry(date, cursor.getDouble(2), cursor.getInt(3)))
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
