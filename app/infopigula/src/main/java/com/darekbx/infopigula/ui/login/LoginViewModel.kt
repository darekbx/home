package com.darekbx.infopigula.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.infopigula.domain.LoginUseCase
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

    fun updateEmail(input: String)
    fun updatePassword(input: String)

    fun isEmailValid(): Boolean
    fun isPasswordValid(): Boolean

    fun login()

    fun reset()
}

@HiltViewModel
class DefaultLoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel(), LoginViewModel {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    override val uiState: Flow<LoginUiState>
        get() = _uiState

    override var email by mutableStateOf("")

    override var password by mutableStateOf("")

    override fun updateEmail(input: String) {
        email = input
    }

    override fun updatePassword(input: String) {
        password = input
    }

    override fun isEmailValid(): Boolean = email.isNotBlank()

    override fun isPasswordValid(): Boolean = password.isNotBlank()

    override fun login() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.InProgress
            val result = loginUseCase.invoke(email, password)
            _uiState.value =
                if (result.isSuccess) {
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
