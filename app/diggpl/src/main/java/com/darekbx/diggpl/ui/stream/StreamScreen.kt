package com.darekbx.diggpl.ui.stream

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.diggpl.data.remote.*
import com.darekbx.diggpl.ui.*
import com.darekbx.diggpl.ui.saved.SavedViewModel

@Composable
fun StreamScreen(
    tagName: String,
    modifier: Modifier = Modifier,
    streamViewModel: StreamViewModel = hiltViewModel(),
    savedViewModel: SavedViewModel = hiltViewModel(),
    openStreamItem: (StreamItem) -> Unit = { }
) {
    fun LazyListState.isScrolledToEnd() =
        layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

    val tagStream = streamViewModel.streamItems
    var page by remember { mutableStateOf(1) }
    val state = rememberLazyListState()
    val isAtBottom = state.isScrolledToEnd()
    val uiState by streamViewModel.uiState

    LaunchedEffect(tagName) {
        page = 1
        streamViewModel.loadTags(tagName, page, true)
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom && streamViewModel.hasNextPage) {
            page += 1
            streamViewModel.loadTags(tagName, page)
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val context = LocalContext.current
        LazyColumn(state = state) {
            items(items = tagStream) { item ->
                StreamView(
                    item,
                    openStreamItem = { openStreamItem(it) },
                    onLongClick = {
                        savedViewModel.add(it)
                        showAddedToast(context)
                    }
                )
            }
        }
        when (uiState) {
            is UiState.InProgress -> LoadingProgress()
            is UiState.Error -> ErrorMessage((uiState as UiState.Error).message)
            is UiState.Idle -> { /* Do nothing */}
        }
    }
}
