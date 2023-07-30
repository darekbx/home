package com.darekbx.diggpl.ui.homepage

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.darekbx.common.ui.isScrolledToEnd
import com.darekbx.common.ui.observeAsState
import com.darekbx.diggpl.data.remote.StreamItem
import com.darekbx.diggpl.ui.ErrorMessage
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
    var page by remember { mutableStateOf(1) }
    val state = rememberLazyListState()
    val isAtBottom by remember { derivedStateOf { state.isScrolledToEnd() } }
    val uiState by homePageViewModel.uiState

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()
    var wasResumed by remember { mutableStateOf(false) }
    LaunchedEffect(lifecycleState.value) {
        if (!wasResumed && lifecycleState.value == Lifecycle.Event.ON_RESUME) {
            wasResumed = true
            page = 1
            homePageViewModel.reset()
            homePageViewModel.loadHomepage(page)
        } else if (lifecycleState.value == Lifecycle.Event.ON_PAUSE) {
            wasResumed = false
        }
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
    }
}
