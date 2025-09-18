package com.darekbx.emailbot.imap

import com.darekbx.emailbot.model.ConfigurationInfo
import com.darekbx.emailbot.repository.storage.EncryptedConfiguration
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.UIDFolder

class EmailOperations(
    private val connection: Connection,
    private val encryptedConfiguration: EncryptedConfiguration
) {

    suspend fun removeEmail(vararg messageNumber: Int): Int {
        val configuration: ConfigurationInfo? = encryptedConfiguration.loadConfiguration()
        if (configuration == null) {
            throw IllegalStateException("Configuration not found")
        }

        connection.connect(configuration).use { store ->
            val inbox = store.getFolder("INBOX")
            inbox.open(Folder.READ_WRITE)
            if (inbox is UIDFolder) {
                inbox.getMessages(messageNumber).forEach { message ->
                    message.setFlag(Flags.Flag.DELETED, true)
                }
                val removedMessages = inbox.expunge()
                return removedMessages.size
            }
        }

        return -1
    }
}
