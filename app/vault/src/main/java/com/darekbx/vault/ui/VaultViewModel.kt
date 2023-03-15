package com.darekbx.vault.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.vault.data.VaultRepository
import com.darekbx.vault.data.model.Vault
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val vaultRepository: VaultRepository
) : ViewModel() {

    fun getItems() = vaultRepository.getItems()

    fun getItem(id: Long) = flow {
        emit(vaultRepository.getItem(id))
    }

    fun add(vault: Vault) {
        viewModelScope.launch {
            vaultRepository.add(vault)
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            vaultRepository.delete(id)
        }
    }

    fun validatePin(pin: String): Boolean {
        return vaultRepository.validatePin(pin)
    }

    fun persistPin(pin: String) {
        vaultRepository.persistPin(pin)
    }

    fun prepareLegacyData() {
        viewModelScope.launch {
            vaultRepository.prepareLegacyData()
        }
    }
}
