package com.darekbx.stocks.widget

import kotlinx.serialization.Serializable

@Serializable
sealed interface StocksInfo {

    @Serializable
    object Loading : StocksInfo

    @Serializable
    data class Unavailable(val message: String) : StocksInfo

    @Serializable
    data class Available(
        val plnUsd: Double,
        val plnEur: Double
    ) : StocksInfo
}
