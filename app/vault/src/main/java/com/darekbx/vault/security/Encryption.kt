package com.darekbx.vault.security

import android.util.Base64
import java.math.BigInteger
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Encryption {

    private companion object {
        val CHARSET = Charsets.UTF_8
        val iv = "pz:tX7%4h*Nj8.[U"
        val pinValidationEncodedSecret = "ZuFUc9et1GKwFtydy+2hGA=="

    }

    fun validatePin(pin: String): Boolean {
        return try {
            val result = decode(pin, pinValidationEncodedSecret)
            result == "secret!"
        } catch (e: Exception) {
            false
        }
    }

    fun encode(key: String, rawData: String): String {
        val ivParameterSpec = IvParameterSpec(iv.toByteArray())
        val secretKey = SecretKeySpec(key.md5().toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        return Base64.encodeToString(cipher.doFinal(rawData.toByteArray(CHARSET)), Base64.DEFAULT)
    }

    fun decode(key: String, encodedData: String): String {
        val ivParameterSpec = IvParameterSpec(iv.toByteArray())
        val secretKey = SecretKeySpec(key.md5().toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        return String(cipher.doFinal(Base64.decode(encodedData, Base64.DEFAULT)))
    }

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(this.toByteArray())).toString(16).padStart(32, '0')
    }
}
