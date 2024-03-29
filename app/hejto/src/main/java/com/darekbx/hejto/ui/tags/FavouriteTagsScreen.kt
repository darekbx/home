package com.darekbx.hejto.ui.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.hejto.data.local.model.FavouriteTag
import com.darekbx.hejto.ui.HejtoTheme
import com.darekbx.hejto.ui.posts.ErrorMessage
import com.darekbx.hejto.ui.posts.LoadingProgress
import com.darekbx.hejto.ui.tags.viewmodel.TagsViewModel
import com.darekbx.hejto.ui.tags.viewmodel.UiState

@Composable
fun FavouriteTagsScreen(
    tagsViewModel: TagsViewModel = hiltViewModel(),
    openTagsList: () -> Unit = { },
    openTag: (name: String) -> Unit = { }
) {
    val favouriteTags = tagsViewModel.favouriteTags

    LaunchedEffect(Unit) {
        tagsViewModel.loadFavouritesTags()
    }

    Column(Modifier.fillMaxWidth()) {
        FavouriteTagsList(
            tagsViewModel,
            Modifier.weight(1F),
            favouriteTags
        ) { name, entriesCount ->
            tagsViewModel.markOpenedTag(name, entriesCount)
            openTag(name)
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 9.dp, end = 9.dp),
            shape = RoundedCornerShape(8.dp),
            onClick = { openTagsList() }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
private fun FavouriteTagsList(
    tagsViewModel: TagsViewModel,
    modifier: Modifier,
    tags: List<FavouriteTag>,
    openTag: (String, Int) -> Unit
) {
    val uiState by tagsViewModel.uiState
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is UiState.InProgress -> LoadingProgress()
            is UiState.Error -> ErrorMessage((uiState as UiState.Error).message)
            is UiState.Idle -> { /* Do nothing */ }
        }
        LazyColumn(
            reverseLayout = true,
            modifier = modifier
                .fillMaxSize()
                .padding(top = 4.dp, bottom = 0.dp)
        ) {
            items(items = tags) { item ->
                FavouriteTagView(item, openTag, tagsViewModel::removeFavouriteTag)
            }
        }
    }
}

@Composable
private fun FavouriteTagView(
    tag: FavouriteTag,
    openTag: (String, Int) -> Unit,
    removeTag: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable { openTag(tag.name, tag.entriesCount) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "#${tag.name}",
            modifier = Modifier
                .padding(start = 8.dp)
                .width(160.dp),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (tag.hasNewEntries()) {
                Text(
                    text = "${tag.newEntriesCount} new entries",
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W700
                )
            } else {
                Text(
                    text = "${tag.entriesCount} entries",
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                modifier = Modifier
                    .width(32.dp)
                    .clickable { removeTag(tag.name) },
                imageVector = if (tag.failedToLoad) Icons.Default.Warning else Icons.Default.Favorite,
                contentDescription = "favourite",
                tint = if (tag.failedToLoad) Color.Red else MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview
@Composable
private fun TagViewPreview() {
    HejtoTheme {
        FavouriteTagView(tag = FavouriteTag("motoryzacja", 512, 21), { _, _ -> }, { })
    }
}
