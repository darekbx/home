package com.darekbx.notebookcheckreader.ui.favourites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darekbx.notebookcheckreader.model.RssItem
import com.darekbx.notebookcheckreader.ui.news.ListItem

@Composable
fun Favourites(viewModel: FavouritesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetch()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val state = uiState) {
            is FavouritesUiState.Loading -> LoadingSpinner()
            is FavouritesUiState.Error -> ErrorMessage(state.message)
            is FavouritesUiState.Success -> FavouritesContent(
                rssItems = state.items,
                onRemoveFromFavourites = { itemId -> viewModel.removeFromFavourites(itemId) }
            )
        }
    }
}

@Composable
private fun BoxScope.FavouritesContent(
    rssItems: List<RssItem>,
    onRemoveFromFavourites: (String) -> Unit = {}
) {
    if (rssItems.isEmpty()) {
        Text(
            text = "No favourites yet",
            style = MaterialTheme.typography.bodySmall,
        )
        return
    }

    val lazyState = rememberLazyGridState()

    LazyVerticalGrid(
        state = lazyState,
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .padding(start = 4.dp, end = 4.dp),
        columns = GridCells.Fixed(1),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(rssItems, key = { _, item -> item.localId }) { index, item ->
            item.isRead = false
            ListItem(item, index) { onRemoveFromFavourites(item.localId) }
        }
    }
}

@Composable
private fun LoadingSpinner() {
    CircularProgressIndicator(Modifier.size(64.dp))
}

@Composable
private fun ErrorMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
    )
}
