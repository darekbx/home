package com.darekbx.vault.data

import com.darekbx.storage.BuildConfig
import com.darekbx.storage.legacy.OwnSpaceHelper
import com.darekbx.storage.vault.VaultDao
import com.darekbx.storage.vault.VaultDto
import com.darekbx.vault.data.model.Vault
import com.darekbx.vault.security.Encryption
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VaultRepository @Inject constructor(
    private val ownSpaceHelper: OwnSpaceHelper?,
    private val vaultDao: VaultDao,
    private val encryption: Encryption
) {
    companion object {
        private var storedPin: String? = null
    }

    fun validatePin(pin: String): Boolean {
        return encryption.validatePin(pin)
    }

    fun persistPin(pin: String) {
        storedPin = pin
    }

    fun reset() {
        storedPin = null
    }

    suspend fun delete(id: Long) {
        vaultDao.delete(id)
    }

    suspend fun add(vault: Vault) {
        with(vault) {
            vaultDao.add(
                VaultDto(
                    null,
                    key,
                    encryption.encode(storedPin!!, account),
                    encryption.encode(storedPin!!, password)
                )
            )
        }
    }

    fun getItems() =
        vaultDao.getItems().map { list ->
            list.map { Vault(it.id, it.key) }
        }

    suspend fun getItem(id: Long) =
        try {
            vaultDao.getItem(id).let {
                Vault(
                    it.id,
                    it.key,
                    encryption.decode(storedPin!!, it.account),
                    encryption.decode(storedPin!!, it.password)
                )
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            null
        }

    suspend fun prepareLegacyData() {
        //vaultDao.deleteAll()
        if (vaultDao.count() == 0) {
            fillVaultFromLegacyDatabase()
        }
    }

    private suspend fun fillVaultFromLegacyDatabase() {
        val listOfLegacyEntries = ownSpaceHelper?.getVaultEntries() ?: emptyList()
        vaultDao.addAll(listOfLegacyEntries.map {
            VaultDto(null, it.key, it.account, it.password)
        })
    }
}
