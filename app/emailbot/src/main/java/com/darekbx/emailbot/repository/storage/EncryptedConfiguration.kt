package com.darekbx.emailbot.repository.storage

import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.first
import androidx.datastore.core.DataStore
import com.darekbx.emailbot.model.ConfigurationInfo

class EncryptedConfiguration(
    private val dataStore: DataStore<Preferences>,
    private val cryptoUtils: CryptoUtils
) {

    private val emailKey = stringPreferencesKey("email")
    private val passwordKey = stringPreferencesKey("password")
    private val imapHostKey = stringPreferencesKey("imap_host")
    private val imapPortKey = stringPreferencesKey("imap_port")

    suspend fun saveConfiguration(info: ConfigurationInfo) {
        dataStore.edit { prefs ->
            prefs[emailKey] = cryptoUtils.encrypt(info.email)
            prefs[passwordKey] = cryptoUtils.encrypt(info.password)
            prefs[imapHostKey] = cryptoUtils.encrypt(info.imapHost)
            prefs[imapPortKey] = cryptoUtils.encrypt(info.imapPort.toString())
        }
    }

    suspend fun loadConfiguration(): ConfigurationInfo? {
        val prefs = dataStore.data.first()

        val emailEnc = prefs[emailKey]
        val passwordEnc = prefs[passwordKey]
        val imapHostEnc = prefs[imapHostKey]
        val imapPortEnc = prefs[imapPortKey]

        return if (emailEnc != null && passwordEnc != null && imapHostEnc != null && imapPortEnc != null) {
            ConfigurationInfo(
                email = cryptoUtils.decrypt(emailEnc),
                password = cryptoUtils.decrypt(passwordEnc),
                imapHost = cryptoUtils.decrypt(imapHostEnc),
                imapPort = cryptoUtils.decrypt(imapPortEnc).toInt()
            )
        } else {
            null
        }
    }
}
