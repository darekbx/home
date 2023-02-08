package com.darekbx.lifetimememo.screens.category

sealed class CategoryUiState {
    object InProgress : CategoryUiState()
    object Done : CategoryUiState()
    object Error : CategoryUiState()
}
