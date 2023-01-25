package com.darekbx.stocks.data

import android.util.Log
import com.darekbx.stocks.BuildConfig
import com.darekbx.stocks.data.remote.CurrencyService
import com.darekbx.stocks.widget.StocksInfo
import com.darekbx.storage.stocks.CurrencyDto
import com.darekbx.storage.stocks.RateDto
import com.darekbx.storage.stocks.StocksDao
import javax.inject.Inject
import kotlin.random.Random

class StocksRepository @Inject constructor(
    private val stocksDao: StocksDao,
    private val currencyService: CurrencyService,
    private val responseParser: ResponseParser
) {
    companion object {
        private val TAG = "StocksRepository"
    }

    suspend fun refreshCurrency(currency: CurrencyDto) {
        try {
            val response = currencyService.getCurrencyInfo(currency.queryParam)
            val value = responseParser.parseResponse(response)
            if (value != null) {
                Log.v(TAG, "${currency.label}: $value")
                stocksDao.add(RateDto(currencyId = currency.id!!, value = value))
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
    }

    suspend fun currencies() = stocksDao.getCurrencies()

    suspend fun rates(currencyId: Long) = stocksDao.getRates(currencyId)

    suspend fun clearDataForImport() {
        stocksDao.deleteCurrencies()
        stocksDao.deleteRates()
    }

    /**
     * @return New row id
     */
    suspend fun addCurrency(label: String, queryParam: String): Long {
        return stocksDao.add(CurrencyDto(label = label, queryParam = queryParam))
    }

    suspend fun addRates(currencyId:Long, data: List<Double>) {
        data.forEach { rate ->
            stocksDao.add(RateDto(currencyId = currencyId, value = rate))
        }
    }

    suspend fun getStocksInfo(delay: Long = Random.nextInt(1, 3) * 1000L): StocksInfo {
        // Simulate network
        // loading
        if (delay > 0) {
            kotlinx.coroutines.delay(delay)
        }
        return StocksInfo.Available(
            plnEur = Random.nextDouble() * 4.6,
            plnUsd = Random.nextDouble() * 4.2
        )
    }
}
