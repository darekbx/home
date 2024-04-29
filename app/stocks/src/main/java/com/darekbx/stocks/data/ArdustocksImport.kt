package com.darekbx.stocks.data

import android.content.Context
import androidx.collection.mutableScatterMapOf
import com.darekbx.stocks.BuildConfig
import java.io.BufferedReader

class ArdustocksImport(
    private val stocksRepository: StocksRepository,
    private val context: Context
) {

    class Source(val type: StockType, val queryParam: String, val file: String)

    companion object {
        private val SOURCES = listOf(
            Source(StockType.PLN_USD, "usdpln", "pln_usd.txt"),
            Source(StockType.PLN_EUR, "eurpln", "pln_eur.txt"),
            Source(StockType.BTC, "btc.v", "btc.txt"),
            Source(StockType.GOLD, "xaupln", "gold.txt")
        )
    }

    suspend fun addCustom(stockType: StockType, query: String) {
        stocksRepository.addCurrency(stockType.label, query)
    }

    suspend fun importFromCsv() {
        stocksRepository.clearDataForImport()
        val idMap = mutableScatterMapOf<Long, Long>()
        var index = 1L
        for (source in SOURCES) {
            val currencyId = stocksRepository.addCurrency(source.type.label, source.queryParam)
            idMap.put(index++, currencyId)
        }

        // id, currency_id, value
        val contents = readAssetContents("rates.csv")
        contents?.lines()?.forEach { line ->
            val data = line.split(',')
            stocksRepository.addRate(idMap[data[1].toLong()]!!, data[2].toDouble())
        }
    }

    /**
     * To import data from ArduStocks, place those files in assets/ directory:
     *  - pln_usd.txt
     *  - pln_eur.txt
     *  - btc.txt
     *  - gold.txt
     */
    suspend fun importFromArdustocks() {
        stocksRepository.clearDataForImport()
        for (source in SOURCES) {
            val contents = readAssetContents(source.file)
                ?: continue
            val data = splitContents(contents)
            val currencyId = stocksRepository.addCurrency(source.type.label, source.queryParam)
            stocksRepository.addRates(currencyId, data)
        }
    }

    private fun splitContents(contents: String): List<Double> {
        return contents
            .split(',')
            .filter { it.isNotBlank() }
            .map { it.toDouble() }
    }

    private fun readAssetContents(fileName: String): String? {
        try {
            if (!containsFile(fileName)) {
                return null
            }
            return context
                .assets
                .open(fileName)
                .bufferedReader()
                .use(BufferedReader::readText)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            return null
        }
    }

    private fun containsFile(fileName: String): Boolean {
        val assets = context.assets.list("") ?: return false
        return assets.contains(fileName)
    }
}
