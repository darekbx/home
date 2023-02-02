package com.darekbx.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.darekbx.storage.hejto.CommunityInfoDto
import com.darekbx.storage.hejto.FavouriteTagDto
import com.darekbx.storage.hejto.HejtoDao
import com.darekbx.storage.hejto.SavedSlugDto
import com.darekbx.storage.stocks.CurrencyDto
import com.darekbx.storage.stocks.StocksDao
import com.darekbx.storage.stocks.RateDto

@Database(
    entities = [
        CurrencyDto::class,
        RateDto::class,
        FavouriteTagDto::class,
        SavedSlugDto::class,
        CommunityInfoDto::class
    ],
    exportSchema = true,
    version = 4
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
                        `entries_count` INTEGER NOT NULL, 
                        `id` INTEGER NULL, 
                         PRIMARY KEY(`id`)
                       )"""
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS `saved_slug` (
                        |`id` INTEGER PRIMARY KEY AUTOINCREMENT, 
                        |`slug` TEXT NOT NULL, 
                        |`title` TEXT NOT NULL, 
                        |`content` TEXT NOT NULL
                        |)""".trimMargin()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS `community_info` (
                        |`id` INTEGER PRIMARY KEY AUTOINCREMENT, 
                        |`slug` TEXT NOT NULL, 
                        |`posts_count` INTEGER NOT NULL
                        |)""".trimMargin()
                )
            }
        }
    }
}
