package com.darekbx.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.darekbx.storage.lifetimememo.BackupDao
import com.darekbx.storage.lifetimememo.MemoDao
import com.darekbx.storage.lifetimememo.CategoryDto
import com.darekbx.lifetimememo.data.dto.ContainerDto
import com.darekbx.storage.lifetimememo.LocationDto
import com.darekbx.storage.lifetimememo.MemoDto
import com.darekbx.storage.hejto.CommunityInfoDto
import com.darekbx.storage.hejto.FavouriteTagDto
import com.darekbx.storage.hejto.HejtoDao
import com.darekbx.storage.hejto.SavedSlugDto
import com.darekbx.storage.lifetimememo.SearchDao
import com.darekbx.storage.stocks.CurrencyDto
import com.darekbx.storage.stocks.StocksDao
import com.darekbx.storage.stocks.RateDto

@Database(
    entities = [
        CurrencyDto::class,
        RateDto::class,
        FavouriteTagDto::class,
        SavedSlugDto::class,
        CommunityInfoDto::class,
        CategoryDto::class,
        LocationDto::class,
        MemoDto::class,
        ContainerDto::class
    ],
    exportSchema = true,
    version = 5
)
abstract class HomeDatabase : RoomDatabase() {

    abstract fun stocksDao(): StocksDao

    abstract fun hejtoDao(): HejtoDao

    abstract fun memoDao(): MemoDao

    abstract fun searchDao(): SearchDao

    abstract fun backupDao(): BackupDao

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

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `category` (`uid` TEXT NOT NULL, `name` TEXT NOT NULL, PRIMARY KEY(`uid`))")
                database.execSQL("CREATE TABLE IF NOT EXISTS `container` (`uid` TEXT NOT NULL, `parent_uid` TEXT, `title` TEXT NOT NULL, `subtitle` TEXT, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`uid`))")
                database.execSQL("CREATE TABLE IF NOT EXISTS `location` (`uid` TEXT NOT NULL, `memo_id` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, PRIMARY KEY(`uid`))")
                database.execSQL("CREATE TABLE IF NOT EXISTS `memo` (`uid` TEXT NOT NULL, `container_uid` TEXT, `title` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `category_uid` TEXT NOT NULL, `subtitle` TEXT, `description` TEXT, `link` TEXT, `date_time` INTEGER, `flag` INTEGER, `reminder` INTEGER, PRIMARY KEY(`uid`))")
            }
        }
    }
}
