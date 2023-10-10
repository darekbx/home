package com.darekbx.infopigula.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.infopigula.domain.CurrentUserUseCase
import com.darekbx.infopigula.domain.LogoutUseCase
import com.darekbx.infopigula.domain.UserNotLoggedInException
import com.darekbx.infopigula.model.User
import com.darekbx.infopigula.repository.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DrawerMenuUiState {
    object Idle : DrawerMenuUiState()
    object InProgress : DrawerMenuUiState()
    object NotLoggedIn : DrawerMenuUiState()
    class Failed(val message: String) : DrawerMenuUiState()
    class Done(val user: User) : DrawerMenuUiState()
    object LoggedOut : DrawerMenuUiState()
}

interface DrawerMenuViewModel {

    val uiState: Flow<DrawerMenuUiState>

    fun logout()
}

@HiltViewModel
class DefaultDrawerMenuViewModel @Inject constructor(
    private val currentUserUseCase: CurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val session: Session
) : ViewModel(), DrawerMenuViewModel {

    private val _uiState = MutableStateFlow<DrawerMenuUiState>(DrawerMenuUiState.Idle)
    override val uiState: Flow<DrawerMenuUiState>
        get() = _uiState

    init {
        viewModelScope.launch {
            initializeUser()

            /**
             * Listen for sesison changes.
             * When user is authorized, fetch current user
             */
            session.isUserActive.consumeEach { isUserActive ->
                if (isUserActive) {
                    initializeUser()
                }
            }
        }
    }

    private suspend fun initializeUser() {
        _uiState.value = DrawerMenuUiState.InProgress
        val result = currentUserUseCase.invoke()
        if (result.isSuccess) {
            _uiState.value = DrawerMenuUiState.Done(result.getOrThrow())
        } else {
            if (result.exceptionOrNull() is UserNotLoggedInException) {
                _uiState.value = DrawerMenuUiState.NotLoggedIn
            } else {
                _uiState.value = DrawerMenuUiState.Failed(
                    result.exceptionOrNull()?.message ?: "Unknown error"
                )
            }
        }
    }

    override fun logout() {
        viewModelScope.launch {
            _uiState.value = DrawerMenuUiState.InProgress
            delay(500L)
            logoutUseCase.invoke()
            _uiState.value = DrawerMenuUiState.LoggedOut
        }
    }
}