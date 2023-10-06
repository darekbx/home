package com.darekbx.infopigula.repository

import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

class Session @Inject constructor() {

    val isUserActive = Channel<Boolean>()

    fun setUserActive() {
        isUserActive.trySend(true)
    }

    fun setLoggedOut() {
        isUserActive.trySend(false)
    }
}