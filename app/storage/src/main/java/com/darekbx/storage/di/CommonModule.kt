package com.darekbx.storage.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.darekbx.storage.HomeDatabase
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_10_11
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
import com.darekbx.storage.fuel.FuelDao
import com.darekbx.storage.hejto.HejtoDao
import com.darekbx.storage.legacy.OwnSpaceHelper
import com.darekbx.storage.lifetimememo.BackupDao
import com.darekbx.storage.lifetimememo.MemoDao
import com.darekbx.storage.lifetimememo.SearchDao
import com.darekbx.storage.stocks.StocksDao
import com.darekbx.storage.notes.NotesDao
import com.darekbx.storage.task.TaskDao
import com.darekbx.storage.weight.WeightDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "home_preferences")

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideOwnSpaceHelper(@ApplicationContext context: Context): OwnSpaceHelper? {
        return try {
            OwnSpaceHelper(context)
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
            .build()
    }
}
