package com.darekbx.diggpl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.diggpl.data.remote.Tag
import com.darekbx.diggpl.ui.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HomeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Test()
                }
            }
        }
    }
}

@Composable
fun Test(authViewModel: AuthViewModel = hiltViewModel()) {

    fun LazyListState.isScrolledToEnd() =
        layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

    val tagName = "lego"

    val tagStream = authViewModel.tagStream
    var page by remember { mutableStateOf(1) }
    val state = rememberLazyListState()
    val isAtBottom = state.isScrolledToEnd()
    val uiState by authViewModel.uiState

    LaunchedEffect(Unit) {
        authViewModel.loadTags(tagName, page)
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom && authViewModel.hasNextPage) {
            page += 1
            authViewModel.loadTags(tagName, page)
        }
    }

    ///
    /// display list
}
