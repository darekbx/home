package com.darekbx.vault.data.model

data class Vault(
    val id: Long?,
    val key: String,
    val account: String = "",
    val password: String = ""
)