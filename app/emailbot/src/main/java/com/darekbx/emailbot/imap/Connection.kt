package com.darekbx.emailbot.imap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Session
import javax.mail.Store
import com.darekbx.emailbot.model.ConfigurationInfo

class Connection {

    fun connect(configurationInfo: ConfigurationInfo): Store {
        val session = createSession(configurationInfo)
        val store: Store = session.getStore(PROTOCOL)

        store.connect(
            configurationInfo.imapHost,
            configurationInfo.imapPort,
            configurationInfo.email,
            configurationInfo.password
        )

        return store
    }

    suspend fun verifyConfiguration(configurationInfo: ConfigurationInfo): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val store = connect(configurationInfo)
                store.close()
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun createSession(configurationInfo: ConfigurationInfo): Session {
        val props = configurationInfo.toProperties()
        return Session.getInstance(props, null)
    }

    private fun ConfigurationInfo.toProperties() =
        Properties().apply {
            put("mail.store.protocol", PROTOCOL)
            put("mail.imap.host", this@toProperties.imapHost)
            put("mail.imap.port", this@toProperties.imapPort.toString())
            put("mail.imap.starttls.enable", "true")
            put("mail.imap.ssl.trust", "*")
            put("mail.imap.ssl.enable", "true")
        }

    companion object {
        private const val PROTOCOL = "imap"
    }
}
