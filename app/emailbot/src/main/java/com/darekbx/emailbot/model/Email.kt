package com.darekbx.emailbot.model

data class Email(
    val messageId: String?,
    val messageNumber: Int,
    val from: String,
    val to: String,
    val subject: String,
    val content: EmailContent,
    val dateTime: String
) {
    var isSpam: Boolean = false
}

sealed class EmailContent {
    data class Text(val text: String) : EmailContent()
    data class Html(val html: String) : EmailContent()
    data class Mixed(val textContent: String, val htmlContent: String?) : EmailContent()
    data object Unknown : EmailContent()
}
