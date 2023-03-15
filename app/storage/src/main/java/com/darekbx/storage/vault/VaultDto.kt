package com.darekbx.storage.vault

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault")
class VaultDto(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "account") val account: String,
    @ColumnInfo(name = "password") val password: String
)
