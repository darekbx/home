package com.darekbx.lifetimememo.screens.memos

sealed class MemosUiState {
    object InProgress : MemosUiState()
    object Done : MemosUiState()
    object Error : MemosUiState()
}
