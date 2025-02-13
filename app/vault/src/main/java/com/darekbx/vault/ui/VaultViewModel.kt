package com.darekbx.vault.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.vault.data.VaultRepository
import com.darekbx.vault.data.model.Vault
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val vaultRepository: VaultRepository
) : ViewModel() {

    var inProgress = mutableStateOf(false)

    fun getItems() = vaultRepository.getItems()

    fun getItem(id: Long) = flow {
        emit(vaultRepository.getItem(id))
    }

    fun update(id: Long, key: String, account: String, password: String) {
        viewModelScope.launch {
            inProgress.value = true
            vaultRepository.update(id, key, account, password)
            delay(500) // For better UX
            inProgress.value = false
        }
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
