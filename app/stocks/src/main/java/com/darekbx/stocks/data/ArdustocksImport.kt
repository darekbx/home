package com.darekbx.stocks.data

import android.content.Context
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
