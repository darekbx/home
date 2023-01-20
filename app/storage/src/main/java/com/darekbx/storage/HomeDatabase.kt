package com.darekbx.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darekbx.storage.stocks.CurrencyDto
import com.darekbx.storage.stocks.StocksDao
import com.darekbx.storage.stocks.RateDto

@Database(
    entities = [
        CurrencyDto::class,
        RateDto::class,
    ],
    exportSchema = false,
    version = 1
)
abstract class HomeDatabase : RoomDatabase() {

    abstract fun stocksDao(): StocksDao

    companion object {
        val DB_NAME = "home_db"
    }
}