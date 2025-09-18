package com.darekbx.notebookcheckreader.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.notebookcheckreader.domain.AddRemoveToFavouritesUseCase
import com.darekbx.notebookcheckreader.domain.FetchFavouriteItemsUseCase
import com.darekbx.notebookcheckreader.model.RssItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FavouritesUiState {
    object Loading : FavouritesUiState
    data class Error(val message: String) : FavouritesUiState
    data class Success(val items: List<RssItem>) : FavouritesUiState
}

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val fetchFavouriteItemsUseCase: FetchFavouriteItemsUseCase,
    private val addRemoveToFavouritesUseCase: AddRemoveToFavouritesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<FavouritesUiState>(FavouritesUiState.Loading)
    val uiState: StateFlow<FavouritesUiState> = _uiState.asStateFlow()

    fun fetch() {
        viewModelScope.launch {
            _uiState.emit(FavouritesUiState.Loading)
            try {
                val items = fetchFavouriteItemsUseCase()
                _uiState.emit(FavouritesUiState.Success(items))
            } catch (e: Exception) {
                _uiState.emit(FavouritesUiState.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun removeFromFavourites(itemId: String) {
        viewModelScope.launch {
            addRemoveToFavouritesUseCase(itemId)
            fetch()
        }
    }
}
