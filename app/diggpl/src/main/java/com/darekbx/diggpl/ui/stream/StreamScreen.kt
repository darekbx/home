@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.diggpl.ui.stream

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.diggpl.data.remote.*
import com.darekbx.diggpl.ui.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StreamScreen(
    modifier: Modifier = Modifier,
    streamViewModel: StreamViewModel = hiltViewModel(),
    openStreamItem: (StreamItem) -> Unit = { }
) {

    fun LazyListState.isScrolledToEnd() =
        layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

    var tempTagName by remember { mutableStateOf("") }
    var tagName by remember { mutableStateOf("wiadomosci") }

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
        LazyColumn(state = state) {
            stickyHeader {
                // TODO remove temporary tag input
                TextField(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                        .height(54.dp),
                    value = tempTagName,
                    onValueChange = { tempTagName = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { tagName = tempTagName }
                    )
                )
            }
            items(items = tagStream) { item ->
                StreamView(item) { openStreamItem(it) }
            }
        }
        when (uiState) {
            is UiState.InProgress -> LoadingProgress()
            is UiState.Error -> ErrorMessage((uiState as UiState.Error).message)
            is UiState.Idle -> { /* Do nothing */  }
        }
    }
}
