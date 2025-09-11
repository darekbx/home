package com.darekbx.storage.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.darekbx.geotracker.repository.PlaceDao
import com.darekbx.geotracker.repository.PointDao
import com.darekbx.geotracker.repository.RouteDao
import com.darekbx.geotracker.repository.TrackDao
import com.darekbx.storage.BuildConfig
import com.darekbx.storage.HomeDatabase
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_10_11
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_11_12
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_12_13
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_13_14
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_14_15
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_15_16
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_16_17
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_17_18
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_1_2
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_2_3
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_3_4
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_4_5
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_5_6
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_6_7
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_7_8
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_8_9
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_9_10
import com.darekbx.storage.books.BookDao
import com.darekbx.storage.diggpl.DiggDao
import com.darekbx.storage.dotpad.DotsDao
import com.darekbx.storage.favourites.FavouritesDao
import com.darekbx.storage.fuel.FuelDao
import com.darekbx.storage.hejto.HejtoDao
import com.darekbx.storage.legacy.DotPadHelper
import com.darekbx.storage.legacy.GeoTrackerHelper
import com.darekbx.storage.legacy.OwnSpaceHelper
import com.darekbx.storage.lifetimememo.BackupDao
import com.darekbx.storage.lifetimememo.MemoDao
import com.darekbx.storage.lifetimememo.SearchDao
import com.darekbx.storage.stocks.StocksDao
import com.darekbx.storage.notes.NotesDao
import com.darekbx.storage.riverstatus.WaterLevelDao
import com.darekbx.storage.spreadsheet.CellDao
import com.darekbx.storage.spreadsheet.SpreadSheetDao
import com.darekbx.storage.task.TaskDao
import com.darekbx.storage.timeline.TimelineDao
import com.darekbx.storage.vault.VaultDao
import com.darekbx.storage.weight.WeightDao
import com.darekbx.storage.words.WordDao
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "home_preferences")

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            }
        )
        .build()

    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun provideGeoTrackerHelper(@ApplicationContext context: Context): GeoTrackerHelper? {
        return try {
            GeoTrackerHelper(context)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Singleton
    @Provides
    fun provideOwnSpaceHelper(@ApplicationContext context: Context): OwnSpaceHelper? {
        return try {
            OwnSpaceHelper(context)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Singleton
    @Provides
    fun provideDotPadHelper(@ApplicationContext context: Context): DotPadHelper? {
        return try {
            DotPadHelper(context)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    fun provideStocksDao(database: HomeDatabase): StocksDao {
        return database.stocksDao()
    }

    @Provides
    fun provideHejtoDao(database: HomeDatabase): HejtoDao {
        return database.hejtoDao()
    }

    @Provides
    fun provideMemoDao(database: HomeDatabase): MemoDao {
        return database.memoDao()
    }

    @Provides
    fun provideSearchDao(database: HomeDatabase): SearchDao {
        return database.searchDao()
    }

    @Provides
    fun provideBackupDao(database: HomeDatabase): BackupDao {
        return database.backupDao()
    }

    @Provides
    fun provideDiggDao(database: HomeDatabase): DiggDao {
        return database.diggDao()
    }

    @Provides
    fun provideFuelDao(database: HomeDatabase): FuelDao {
        return database.fuelDao()
    }

    @Provides
    fun provideTaskDao(database: HomeDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    fun provideNotesDao(database: HomeDatabase): NotesDao {
        return database.notesDao()
    }

    @Provides
    fun provideWeightDao(database: HomeDatabase): WeightDao {
        return database.weightDao()
    }

    @Provides
    fun provideBookDao(database: HomeDatabase): BookDao {
        return database.bookDao()
    }

    @Provides
    fun provideVaultDao(database: HomeDatabase): VaultDao {
        return database.vaultDao()
    }

    @Provides
    fun provideWaterLevelDao(database: HomeDatabase): WaterLevelDao {
        return database.waterLevelDao()
    }

    @Provides
    fun provideDotsDao(database: HomeDatabase): DotsDao {
        return database.dotsDao()
    }

    @Provides
    fun provideTrackDao(database: HomeDatabase): TrackDao {
        return database.trackDao()
    }

    @Provides
    fun providePointDao(database: HomeDatabase): PointDao {
        return database.pointDao()
    }
    @Provides
    fun provideRouteDao(database: HomeDatabase): RouteDao {
        return database.routeDao()
    }

    @Provides
    fun providePlaceDao(database: HomeDatabase): PlaceDao {
        return database.placeDao()
    }

    @Provides
    fun provideTimelineDao(database: HomeDatabase): TimelineDao {
        return database.timelineDao()
    }

    @Provides
    fun provideFavouritesDao(database: HomeDatabase): FavouritesDao {
        return database.favouritesDao()
    }

    @Provides
    fun provideWordsDao(database: HomeDatabase): WordDao {
        return database.wordsDao()
    }

    @Provides
    fun provideCellDao(database: HomeDatabase): CellDao {
        return database.cellDao()
    }

    @Provides
    fun provideSpreadSheetDao(database: HomeDatabase): SpreadSheetDao {
        return database.spreadSheetDao()
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): HomeDatabase {
        return Room
            .databaseBuilder(
                appContext,
                HomeDatabase::class.java,
                HomeDatabase.DB_NAME
            )
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .addMigrations(MIGRATION_3_4)
            .addMigrations(MIGRATION_4_5)
            .addMigrations(MIGRATION_5_6)
            .addMigrations(MIGRATION_6_7)
            .addMigrations(MIGRATION_7_8)
            .addMigrations(MIGRATION_8_9)
            .addMigrations(MIGRATION_9_10)
            .addMigrations(MIGRATION_10_11)
            .addMigrations(MIGRATION_11_12)
            .addMigrations(MIGRATION_12_13)
            .addMigrations(MIGRATION_13_14)
            .addMigrations(MIGRATION_14_15)
            .addMigrations(MIGRATION_15_16)
            .addMigrations(MIGRATION_16_17)
            .addMigrations(MIGRATION_17_18)
            .build()
    }
}
