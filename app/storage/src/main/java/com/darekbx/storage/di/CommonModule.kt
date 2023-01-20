package com.darekbx.storage.di

import android.content.Context
import androidx.room.Room
import com.darekbx.storage.HomeDatabase
import com.darekbx.storage.stocks.StocksDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext as ApplicationContext1

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideStocksDao(database: HomeDatabase): StocksDao {
        return database.stocksDao()
    }

    @Provides
    fun provideDatabase(@ApplicationContext1 appContext: Context): HomeDatabase {
        return Room.databaseBuilder(
            appContext,
            HomeDatabase::class.java,
            HomeDatabase.DB_NAME
        ).build()
    }
}
