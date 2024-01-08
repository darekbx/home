package com.darekbx.diggpl.ui.homepage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.isScrolledToEnd
import com.darekbx.diggpl.data.remote.StreamItem
import com.darekbx.diggpl.ui.ErrorMessage
import com.darekbx.diggpl.ui.ItemsViewedCount
import com.darekbx.diggpl.ui.LoadingProgress
import com.darekbx.diggpl.ui.StreamView
import com.darekbx.diggpl.ui.saved.SavedViewModel
import com.darekbx.diggpl.ui.showAddedToast

@Composable
fun HomePageScreen(
    modifier: Modifier = Modifier,
    homePageViewModel: HomePageViewModel = hiltViewModel(),
    savedViewModel: SavedViewModel = hiltViewModel(),
    openStreamItem: (StreamItem) -> Unit = { }
) {
    val tagStream = homePageViewModel.linkStreamItems
    var page by remember { mutableIntStateOf(1) }
    val state = rememberLazyListState()
    val viewedIds = remember { mutableSetOf<Int>() }
    var viewedCount by remember { mutableIntStateOf(0) }
    val isAtBottom by remember { derivedStateOf { state.isScrolledToEnd() } }
    val uiState by homePageViewModel.uiState

    LaunchedEffect(Unit) {
        homePageViewModel.loadHomepage(page)
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom && homePageViewModel.hasNextPage) {
            page += 1
            homePageViewModel.loadHomepage(page)
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val context = LocalContext.current
        LazyColumn(state = state) {
            items(items = tagStream) { item ->
                viewedIds.add(item.id)
                viewedCount = viewedIds.size
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
            is UiState.Idle -> { /* Do nothing */ }
        }

        ItemsViewedCount(viewedCount)
    }
}
