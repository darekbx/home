package com.darekbx.storage.stocks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StocksDao {

    @Query("DELETE FROM currency WHERE id = :id")
    suspend fun deleteCurrency(id: Long)

    @Query("DELETE FROM currency")
    suspend fun deleteCurrencies()

    @Query("DELETE FROM rate")
    suspend fun deleteRates()

    @Insert
    suspend fun add(rateDto: RateDto)

    @Query("SELECT * FROM rate WHERE currency_id = :currencyId ORDER BY id ASC")
    suspend fun getRates(currencyId: Long): List<RateDto>

    @Insert
    suspend fun add(currencyDto: CurrencyDto) : Long

    @Query("SELECT * FROM currency ORDER BY id DESC")
    suspend fun getCurrencies(): List<CurrencyDto>
}