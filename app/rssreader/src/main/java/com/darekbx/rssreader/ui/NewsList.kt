package com.darekbx.rssreader.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.rssreader.data.model.NewsItem
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun NewsList(newsListViewModel: NewsListViewModel = hiltViewModel()) {

    val uiState by newsListViewModel.uiState

    LaunchedEffect(Unit) {
        newsListViewModel.loadFeed()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is UiState.InProgress -> LoadingProgress((uiState as UiState.InProgress).progress)
            is UiState.Done -> ItemList((uiState as UiState.Done).items)
            is UiState.Error -> ErrorMessage((uiState as UiState.Error).message)
            is UiState.Idle -> { /* Do nothing */ }
        }
    }
}

@Composable
private fun ItemList(items: List<NewsItem>) {
    val localUriHandler = LocalUriHandler.current
    LazyColumn(Modifier.fillMaxWidth()) {
        items(items) { item ->
            ItemView(item = item) { uri ->
                localUriHandler.openUri(uri)
            }
        }
    }
}

@Composable
private fun ItemView(item: NewsItem, onClick: (String) -> Unit = { }) {
    Card(
        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { item.url?.let(onClick) }) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = item.iconId),
                    contentDescription = item.title
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = item.title ?: "",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = item.formattedDate,
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Divider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp), thickness = 1.dp)
            MarkdownText(markdown = item.description ?: "")
        }
    }
}

@Composable
private fun ErrorMessage(error: String) {
    Text(
        text = error,
        color = Color.Red,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(8.dp)
    )
}

@Preview
@Composable
private fun LoadingProgress(progress: Progress = Progress("New Channel", 50.0)) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Loading: ${progress.name}...")
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = (progress.progress / 100F).toFloat(),
            modifier = Modifier.width(256.dp)
        )
    }
}
