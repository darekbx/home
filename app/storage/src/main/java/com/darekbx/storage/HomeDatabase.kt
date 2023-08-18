package com.darekbx.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.darekbx.dotpad.repository.local.entities.DotDto
import com.darekbx.geotracker.repository.PlaceDao
import com.darekbx.geotracker.repository.PointDao
import com.darekbx.geotracker.repository.RouteDao
import com.darekbx.geotracker.repository.TrackDao
import com.darekbx.geotracker.repository.entities.PlaceDto
import com.darekbx.geotracker.repository.entities.PointDto
import com.darekbx.geotracker.repository.entities.RouteDto
import com.darekbx.geotracker.repository.entities.TrackDto
import com.darekbx.storage.lifetimememo.BackupDao
import com.darekbx.storage.lifetimememo.MemoDao
import com.darekbx.storage.lifetimememo.CategoryDto
import com.darekbx.lifetimememo.data.dto.ContainerDto
import com.darekbx.storage.books.BookDao
import com.darekbx.storage.books.BookDto
import com.darekbx.storage.books.ToReadDto
import com.darekbx.storage.diggpl.DiggDao
import com.darekbx.storage.diggpl.SavedEntryDto
import com.darekbx.storage.diggpl.SavedLinkDto
import com.darekbx.storage.diggpl.SavedTagDto
import com.darekbx.storage.dotpad.DotsDao
import com.darekbx.storage.fuel.FuelDao
import com.darekbx.storage.fuel.FuelEntryDto
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
import com.darekbx.storage.notes.NoteDto
import com.darekbx.storage.notes.NotesDao
import com.darekbx.storage.riverstatus.WaterLevelDao
import com.darekbx.storage.riverstatus.WaterLevelDto
import com.darekbx.storage.task.TaskDao
import com.darekbx.storage.task.TaskDto
import com.darekbx.storage.vault.VaultDao
import com.darekbx.storage.vault.VaultDto
import com.darekbx.storage.weight.WeightDao
import com.darekbx.storage.weight.WeightDto

@Database(
    entities = [
        // Stocks
        CurrencyDto::class,
        RateDto::class,
        // Hejto
        FavouriteTagDto::class,
        SavedSlugDto::class,
        CommunityInfoDto::class,
        // LifetimeMemo
        CategoryDto::class,
        LocationDto::class,
        MemoDto::class,
        ContainerDto::class,
        // Digg
        SavedEntryDto::class,
        SavedLinkDto::class,
        SavedTagDto::class,
        // Fuel
        FuelEntryDto::class,
        // Tasks
        TaskDto::class,
        // Notepad
        NoteDto::class,
        // Weight
        WeightDto::class,
        // Books
        BookDto::class,
        ToReadDto::class,
        // PasswordVault
        VaultDto::class,
        // WaterLevel
        WaterLevelDto::class,
        // DotPad
        DotDto::class,
        // GeoTracker
        PointDto::class,
        TrackDto::class,
        PlaceDto::class,
        RouteDto::class
    ],
    exportSchema = true,
    version = 15
)
abstract class HomeDatabase : RoomDatabase() {

    // Stocks
    abstract fun stocksDao(): StocksDao

    // Hejto
    abstract fun hejtoDao(): HejtoDao

    // LifetimeMemo
    abstract fun memoDao(): MemoDao

    abstract fun searchDao(): SearchDao

    abstract fun backupDao(): BackupDao

    // Digg
    abstract fun diggDao(): DiggDao

    // Fuel
    abstract fun fuelDao(): FuelDao

    // Tasks
    abstract fun taskDao(): TaskDao

    // Notes
    abstract fun notesDao(): NotesDao

    // Weight
    abstract fun weightDao(): WeightDao

    // Books
    abstract fun bookDao(): BookDao

    // Vault
    abstract fun vaultDao(): VaultDao

    // Water
    abstract fun waterLevelDao(): WaterLevelDao

    // Dots
    abstract fun dotsDao(): DotsDao

    // Geo tracker
    abstract fun trackDao(): TrackDao

    abstract fun pointDao(): PointDao

    abstract fun placeDao(): PlaceDao

    abstract fun routeDao(): RouteDao

    companion object {
        const val DB_NAME = "home_db"

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

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `saved_entry` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `entry_id` INTEGER NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `saved_link` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `link_id` INTEGER NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `saved_tag` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, `last_date` TEXT NOT NULL)")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `fuel_entry` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `date` TEXT NOT NULL, `liters` REAL NOT NULL, `cost` REAL NOT NULL, `type` INTEGER NOT NULL)")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `task` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, `content` TEXT NOT NULL, `date` TEXT NOT NULL)")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `note` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `contents` TEXT NOT NULL)")
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `weight_entry` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `date` INTEGER NOT NULL, `weight` REAL NOT NULL, `type` INTEGER NOT NULL)")
            }
        }

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `book` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `author` TEXT NOT NULL, `title` TEXT NOT NULL, `flags` TEXT NOT NULL, `year` INTEGER NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `book_to_read` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `author` TEXT NOT NULL, `title` TEXT NOT NULL)")
            }
        }

        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `vault` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `key` TEXT NOT NULL, `account` TEXT NOT NULL, `password` TEXT NOT NULL)")
            }
        }

        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `water_level` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT, `value` INTEGER NOT NULL, `date` TEXT NOT NULL, `station_id` INTEGER NOT NULL)")
            }
        }

        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `dots` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `text` TEXT NOT NULL, `size` INTEGER NOT NULL, `color` INTEGER NOT NULL, `position_x` INTEGER NOT NULL, `position_y` INTEGER NOT NULL, `created_date` INTEGER NOT NULL, `is_archived` INTEGER NOT NULL, `is_sticked` INTEGER NOT NULL, `reminder` INTEGER, `calendar_event_id` INTEGER, `calendar_reminder_id` INTEGER)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_dots_is_archived` ON `dots` (`is_archived`)")
            }
        }

        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `geo_point` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `track_id` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `speed` REAL NOT NULL, `altitude` REAL NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `geo_track` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `label` TEXT, `start_timestamp` INTEGER NOT NULL, `end_timestamp` INTEGER, `distance` REAL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `geo_place` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `label` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `timestamp` INTEGER NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `geo_route` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `label` TEXT NOT NULL, `url` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
            }
        }
    }
}
