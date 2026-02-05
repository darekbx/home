package com.darekbx.spreadsheet.synchronize

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class CompressionUtils {

    fun compressString(value: String): ByteArray {
        return ByteArrayOutputStream().use { outStream ->
             GZIPOutputStream(outStream).use { gzip->
                 gzip.write(value.toByteArray(charset = CHARSET))
             }
             outStream.toByteArray()
         }
    }

    fun decompressToString(data: ByteArray): String {
        return ByteArrayInputStream(data).use { inStream ->
            GZIPInputStream(inStream).use { gzip ->
                gzip.reader(CHARSET).readText()
            }
        }
    }

    companion object {
        private val CHARSET = Charsets.UTF_8
    }
}
