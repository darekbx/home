package com.darekbx.notebookcheckreader.ui.news

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.darekbx.notebookcheckreader.model.RssItem
import com.darekbx.notebookcheckreader.repository.DateTimeParser
import com.darekbx.notebookcheckreader.ui.theme.NotebookcheckReaderTheme
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun News(viewModel: NewsViewModel = hiltViewModel()) {
    val items by viewModel.itemsFlow().collectAsStateWithLifecycle(emptyList())
    Content(
        rssItems = items,
        markVisibleItems = { visibleItems -> viewModel.markAsRead(visibleItems.toList()) },
        onFavouriteChanged = { itemId -> viewModel.markFavourite(itemId) }
    )
}

@Composable
private fun Content(
    rssItems: List<RssItem>,
    markVisibleItems: (Set<String>) -> Unit = {},
    onFavouriteChanged: (String) -> Unit = {}
) {
    if (rssItems.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No items available",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        return
    }

    val lazyState = rememberLazyGridState()
    var previousItemCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(lazyState) {
        snapshotFlow { lazyState.layoutInfo.visibleItemsInfo.map { it.key as String }.toSet() }
            .distinctUntilChanged()
            .collect { items -> markVisibleItems(items) }
    }

    LaunchedEffect(rssItems.size) {
        if (rssItems.size > previousItemCount && previousItemCount > 0) {
            lazyState.animateScrollToItem(0)
        }
        previousItemCount = rssItems.size
    }

    LazyVerticalGrid(
        state = lazyState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp),
        columns = GridCells.Fixed(1),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(rssItems, key = { _, item -> item.localId }) { index, item ->
            ListItem(item, index) { onFavouriteChanged(item.localId) }
        }
    }
}

@Composable
fun ListItem(item: RssItem, index: Int = 0, onFavouriteChanged: () -> Unit = {}) {
    val uriHandler = LocalUriHandler.current
    Card(
        modifier = Modifier
            .padding(4.dp)
            .padding(top = if (index == 0) 16.dp else 0.dp)
            .padding(start = 32.dp, end = 32.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Box(Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(item.link) }) {
            Image(item)
            TitleDate(item)
            FavouriteIcon(item.isFavourite) { onFavouriteChanged() }
            ReadMark(item)
        }
    }
}

@Composable
private fun Image(item: RssItem) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(item.enclosure)
            .crossfade(true)
            .build(),
        contentDescription = item.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
private fun BoxScope.TitleDate(item: RssItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(start = 8.dp, end = 8.dp, top = 4.dp)
            .align(Alignment.BottomStart)
    ) {
        Text(
            modifier = Modifier,
            maxLines = 2,
            lineHeight = 12.sp,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary,
            text = item.title,
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            modifier = Modifier,
            color = MaterialTheme.colorScheme.onPrimary,
            text = DateTimeParser.formatTimestamp(item.timestamp),
            fontSize = 10.sp,
        )
    }
}

@Composable
private fun BoxScope.ReadMark(item: RssItem) {
    if (item.isRead) {
        Text(
            "Read",
            modifier = Modifier
                .absoluteOffset(x = 58.dp, y = 8.dp)
                .padding(2.dp)
                .align(Alignment.TopEnd)
                .rotate(45F)
                .background(
                    color = Color(0x990033CC),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(start = 64.dp, end = 64.dp),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun BoxScope.FavouriteIcon(isFavourite: Boolean,  onFavouriteChanged: () -> Unit = {}) {
    var isFavourite by remember { mutableStateOf(isFavourite) }
    Box(
        Modifier
            .clickable {
                isFavourite = !isFavourite
                onFavouriteChanged()
            }
            .padding(8.dp)
            .align(Alignment.TopStart)
            .size(24.dp)
    ) {
        val icon = if (isFavourite) {
            Icons.Default.Favorite
        } else {
            Icons.Default.FavoriteBorder
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
        )
    }
}

@Preview
@Composable
private fun ItemPreview() {
    NotebookcheckReaderTheme {
        Content(
            listOf(
                rssItem.copy().apply {
                    localId = "1"
                    isRead = true
                },
                rssItem.copy().apply { localId = "2" },
                rssItem.copy().apply { localId = "3" },
                rssItem.copy().apply { localId = "4" }
            )
        )
    }
}

private val rssItem = RssItem(
    title = "Valve denies Steam data breach, says accounts are secure, Valve denies Steam data breach, says accounts are secure",
    link = "https://www.notebookcheck.net/Valve-denies-Steam-data-breach-says-accounts-are-secure.1016732.0.html",
    description = "Valve has confirmed that recent leaks and rumors about 89 million Steam accounts allegedly hacked are false. The company clarified that it has examined a sample of the leak and that user accounts were secure and unaffected.",
    category = "news",
    timestamp = System.currentTimeMillis() / 1000,
    enclosure = "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc4/Valve-denies-Steam-Data-Breach.jpg"
)
