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

    @Test
    fun extract_ale_value() {
        // Given
        val response = "window.cmp_r('ALE~Allegro.eu SA~Warsaw SE~30.740~0.62%~3|ALE_PE~Allegro.eu SA P/E~~~~3|ALEX.US~Alexander & Baldwin Inc~NYSE~17.3500~0.29%~4|ALE.US~ALLETE Inc~NYSE~55.6900~0.41%~4|ALE_MV~Allegro.eu SA Market Value~~32489.00~0.62%~2|ALE_PB~Allegro.eu SA P/BV~~3.572~0.62%~3|ALEC.US~Alector Inc~NASDAQ~6.160~-6.38%~3|ALE_MV.US~ALLETE Inc Market Value~~3192.2~0.42%~1|ALEX_PB.US~Alexander & Baldwin Inc P/BV~~1.228~0.33%~3|ALEX_PE.US~Alexander & Baldwin Inc P/E~~29.971~0.29%~3');"

        // When
        val value = ResponseParser().parseResponse(response)

        // Then
        assertNotNull(value)
        assertEquals(30.74, value!!, 0.0001)
    }
}