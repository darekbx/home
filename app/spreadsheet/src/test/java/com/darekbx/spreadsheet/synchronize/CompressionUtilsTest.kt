package com.darekbx.spreadsheet.synchronize

import org.junit.Assert.assertEquals
import org.junit.Test

class CompressionUtilsTest {

    @Test
    fun `should compress and decompress string`() {
        // Given
        val compressionUtils = CompressionUtils()
        val testString = "Hello World! ążśźćłóę { \"key\": 1, 'field': 'value' }"

        // When
        val compressedBlob = compressionUtils.compressString(testString)
        val decompressed = compressionUtils.decompressToString(compressedBlob)

        // Then
        assertEquals(testString, decompressed)
    }
}
