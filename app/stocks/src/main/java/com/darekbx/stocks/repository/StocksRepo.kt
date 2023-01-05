package com.darekbx.stocks.repository

import com.darekbx.stocks.widget.StocksInfo
import kotlin.random.Random

object StocksRepo {

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
