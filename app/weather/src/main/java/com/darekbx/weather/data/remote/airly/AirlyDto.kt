package com.darekbx.weather.data.remote.airly

fun Double.format(digits: Int) = "%.${digits}f".format(this)

data class Coordinates(val latitude: Double, val longitude: Double)

data class Address(
    val city: String?,
    val street: String?,
    val number: String?
) {
    override fun toString(): String {
        return "$city, ${street ?: ""} ${number ?: ""}"
    }
}

data class Installation(
    val id: Int,
    val location: Coordinates,
    val address: Address,
    val elevation: Double
)

data class RateLimits(
    val dayLimit: Int,
    val dayRemaining: Int,
    val minuteLimit: Int,
    val minuteRemaining: Int
) {

    val isEmpty: Boolean
        get() = dayLimit == -1 && dayRemaining == -1 && minuteLimit == -1 && minuteRemaining == -1
}

data class Standard(val name: String, val pollutant: String, val limit: Double, val percent: Double)

data class Index(
    val name: String,
    val value: Double,
    val level: String,
    val description: String,
    val advice: String,
    val color: String
)

data class Value(val name: String, val value: Double)

data class Current(val values: List<Value>, val indexes: List<Index>, val standards: List<Standard>)

data class Measurements(val current: Current) {

    var installation: Installation? = null
    var installationId: Int = 0
    var rateLimits: RateLimits? = null

    val airlyIndex: Index
        get() = current.indexes.first()

    val averagePMNorm: String
        get() = "${averagePMNorm()}%"

    val humidity: String
        get() = retrieveValue("HUMIDITY")?.run { "${value.toInt()}%" } ?: ""

    val temperature: String
        get() = retrieveValue("TEMPERATURE")?.run { "${value.format(1)}°" } ?: ""

    private fun averagePMNorm(): Int {
        val pmPrefix = "PM"
        return current.standards
            .asSequence()
            .filter { it.pollutant.startsWith(pmPrefix) }
            .map { it.percent }
            .average()
            .toInt()
    }

    private fun retrieveValue(key: String) = current.values.firstOrNull { it.name == key }
}
