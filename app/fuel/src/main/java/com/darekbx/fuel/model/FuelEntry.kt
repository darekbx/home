package com.darekbx.fuel.model

import com.darekbx.fuel.R

enum class FuelType(val valueNum: Int) {
    ON(0),
    PB95(1);

    fun icon() = when (this) {
        PB95 -> R.drawable.ic_fuel95
        ON -> R.drawable.ic_diesel
    }
}

data class FuelEntry(
    val id: Long,
    val date: String,
    val liters: Double,
    val cost: Double,
    val type: FuelType
) {
    fun pricePerLiter() = cost / liters
}

