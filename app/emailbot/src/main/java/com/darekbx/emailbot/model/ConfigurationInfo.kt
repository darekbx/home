package com.darekbx.emailbot.model

data class ConfigurationInfo(
    val email: String,
    val password: String,
    val imapHost: String,
    val imapPort: Int
) {
    companion object {
        val EMPTY = ConfigurationInfo(
            email = "",
            password = "",
            imapHost = "",
            imapPort = 993
        )
    }
}