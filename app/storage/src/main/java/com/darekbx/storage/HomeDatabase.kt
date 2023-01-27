package com.darekbx.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.darekbx.storage.hejto.FavouriteTagDto
import com.darekbx.storage.hejto.HejtoDao
import com.darekbx.storage.stocks.CurrencyDto
import com.darekbx.storage.stocks.StocksDao
import com.darekbx.storage.stocks.RateDto

@Database(
    entities = [
        CurrencyDto::class,
        RateDto::class,
        FavouriteTagDto::class,
    ],
    exportSchema = false,
    version = 2
)
abstract class HomeDatabase : RoomDatabase() {

    abstract fun stocksDao(): StocksDao

    abstract fun hejtoDao(): HejtoDao

    companion object {
        val DB_NAME = "home_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE `favourite_tag`(
                        `name` TEXT NOT NULL, 
                        `entries_Count` INTEGER NOT NULL, 
                        `id` INTEGER NULL, 
                         PRIMARY KEY(`id`)
                       )"""
                )
            }
        }

    }
}