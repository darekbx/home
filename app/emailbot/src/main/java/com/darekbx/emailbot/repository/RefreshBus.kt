package com.darekbx.emailbot.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RefreshBus {

    private val _timestampFlow = MutableSharedFlow<Long>()
    val timestampFlow = _timestampFlow.asSharedFlow()

    suspend fun publishChanges() {
        _timestampFlow.emit(System.currentTimeMillis())
    }

    fun listenForChanges(): Flow<Long> {
        return timestampFlow
    }
}
