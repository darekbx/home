package com.darekbx.infopigula.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.infopigula.domain.LoginUseCase
import com.darekbx.infopigula.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiState {
    object Idle : LoginUiState()
    object InProgress : LoginUiState()
    class Failed(val message: String) : LoginUiState()
    object Done : LoginUiState()
}

interface LoginViewModel {
    val uiState: Flow<LoginUiState>

    var email: String
    var password: String
    var hasRememberMe: Boolean

    fun updateEmail(input: String)
    fun updatePassword(input: String)
    fun setRememberMe(value: Boolean)

    fun isEmailValid(): Boolean
    fun isPasswordValid(): Boolean

    fun login()

    fun reset()
}

@HiltViewModel
class DefaultLoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel(), LoginViewModel {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    override val uiState: Flow<LoginUiState>
        get() = _uiState

    override var email by mutableStateOf("")

    override var password by mutableStateOf("")

    override var hasRememberMe by mutableStateOf(false)

    override fun setRememberMe(value: Boolean) {
        viewModelScope.launch {
            hasRememberMe = value
            settingsRepository.setRememberMe(value)
        }
    }

    override fun updateEmail(input: String) {
        email = input
    }

    override fun updatePassword(input: String) {
        password = input
    }

    override fun isEmailValid(): Boolean = email.isNotBlank()

    override fun isPasswordValid(): Boolean = password.isNotBlank()

    init {
        viewModelScope.launch {
            hasRememberMe = settingsRepository.hasRememberMe()
            if (hasRememberMe) {
                settingsRepository.loginCredential()?.let {
                    email = it
                }
                settingsRepository.passwordCredential()?.let {
                    password = it
                }
            }
        }
    }

    override fun login() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.InProgress
            val result = loginUseCase.invoke(email, password)
            _uiState.value =
                if (result.isSuccess) {
                    if (hasRememberMe) {
                        settingsRepository.saveLoginCredentials(email, password)
                    }
                    LoginUiState.Done
                } else {
                    LoginUiState.Failed(result.exceptionOrNull()?.message ?: "Unknown error")
                }
        }
    }

    override fun reset() {
        viewModelScope.launch {
            updatePassword("")
            _uiState.value = LoginUiState.Idle
        }
    }
}
