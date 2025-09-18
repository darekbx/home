package com.darekbx.emailbot.imap

import com.darekbx.emailbot.model.ConfigurationInfo
import com.darekbx.emailbot.model.Email
import com.darekbx.emailbot.model.EmailContent
import com.darekbx.emailbot.repository.storage.EncryptedConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.mail.Folder
import javax.mail.Message
import javax.mail.Multipart
import javax.mail.internet.InternetAddress

class FetchEmails(
    private val connection: Connection,
    private val encryptedConfiguration: EncryptedConfiguration
) {

    suspend fun fetch(): List<Email> {
        val configuration: ConfigurationInfo? = encryptedConfiguration.loadConfiguration()
        if (configuration == null) {
            throw IllegalStateException("Configuration not found")
        }

        return withContext(Dispatchers.IO) {
            val emails: MutableList<Email> = mutableListOf()

            connection.connect(configuration).use { store ->
                val inbox = store.getFolder("INBOX")
                inbox.open(Folder.READ_ONLY)

                emails.addAll(
                    inbox.getMessages()
                        .map { message -> createEmail(message) }
                )

                inbox.close(false)
            }

            emails
        }
    }

    private fun createEmail(message: Message): Email = Email(
        messageId = message.messageID(),
        messageNumber = message.messageNumber,
        from = (message.from.firstOrNull() as? InternetAddress)?.address.orEmpty(),
        to = (message.getRecipients(Message.RecipientType.TO)
            ?.firstOrNull() as? InternetAddress)?.address.orEmpty(),
        subject = message.subject.orEmpty(),
        content = extractEmailContent(message),
        dateTime = message.sentDate?.formatAsString() ?: "(not set)"
    )

    private fun extractEmailContent(message: Message): EmailContent {
        return try {
            when {
                message.isMimeType("text/plain") -> EmailContent.Text(message.content.toString())
                message.isMimeType("text/html") -> EmailContent.Html(message.content.toString())
                message.isMimeType("multipart/*") -> extractMultipart(message.content as? Multipart)
                else -> EmailContent.Unknown
            }
        } catch (_: Exception) {
            EmailContent.Unknown
        }
    }

    private fun extractMultipart(multipart: Multipart?): EmailContent {
        if (multipart == null) return EmailContent.Unknown

        var textContent: String? = null
        var htmlContent: String? = null

        for (i in 0 until multipart.count) {
            val part = multipart.getBodyPart(i)
            when {
                part.isMimeType("text/plain") -> textContent = part.content.toString()
                part.isMimeType("text/html") -> htmlContent = part.content.toString()
                part.isMimeType("multipart/*") -> {
                    val nestedContent = extractMultipart(part.content as? Multipart)
                    when (nestedContent) {
                        is EmailContent.Mixed -> {
                            textContent = nestedContent.textContent
                            htmlContent = nestedContent.htmlContent
                        }
                        is EmailContent.Html -> htmlContent = nestedContent.html
                        is EmailContent.Text -> textContent = nestedContent.text
                        else -> {}
                    }
                }
            }
        }

        return if (textContent != null && htmlContent != null) {
            EmailContent.Mixed(textContent, htmlContent)
        } else if (htmlContent != null) {
            EmailContent.Html(htmlContent)
        } else {
            EmailContent.Unknown
        }
    }

    private fun Message.messageID(): String? {
        val headers = this.getHeader("Message-ID")
        return headers?.firstOrNull()?.removePrefix("<")?.removeSuffix(">")
    }

    private fun Date.formatAsString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(this)
    }
}
