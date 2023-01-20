package com.darekbx.stocks

import com.darekbx.stocks.data.ResponseParser
import org.junit.Test

import org.junit.Assert.*

class ResponseTest {

    @Test
    fun extract_response_value() {
        // Given
        val response = "window.cmp_r('<b>USDPLN</b>~U.S. Dollar / Polish Zloty~Currency~4.36210~0.05%~5');"

        // When
        val value = ResponseParser().parseResponse(response)

        // Then
        assertNotNull(value)
        assertEquals(4.36210, value!!, 0.0001)
    }
}