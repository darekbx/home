package com.darekbx.storage.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.darekbx.storage.HomeDatabase
import com.darekbx.storage.HomeDatabase.Companion.MIGRATION_1_2
import com.darekbx.storage.hejto.HejtoDao
import com.darekbx.storage.stocks.StocksDao
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
    fun provideDatabase(@ApplicationContext appContext: Context): HomeDatabase {
        return Room
            .databaseBuilder(
                appContext,
                HomeDatabase::class.java,
                HomeDatabase.DB_NAME
            )
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}
