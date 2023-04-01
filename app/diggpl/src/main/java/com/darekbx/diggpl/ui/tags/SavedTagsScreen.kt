@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.diggpl.ui.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.diggpl.data.local.model.SavedTag
import com.darekbx.diggpl.ui.DiggTheme
import com.darekbx.diggpl.ui.ErrorMessage
import com.darekbx.diggpl.ui.LoadingProgress

@Composable
fun SavedTagsScreen(
    savedTagsViewModel: SavedTagsViewModel = hiltViewModel(),
    openTagsList: () -> Unit = { },
    openTag: (name: String) -> Unit = { }
) {
    val favouriteTags = savedTagsViewModel.savedTags

    LaunchedEffect(Unit) {
        savedTagsViewModel.loadSavedTags()
    }

    Column(Modifier.fillMaxWidth()) {
        SavedTagsList(
            savedTagsViewModel,
            Modifier.weight(1F),
            favouriteTags
        ) { savedTag ->
            openTag(savedTag.name)
        }
        Button(
            modifier = Modifier.fillMaxWidth().padding(start = 9.dp, end = 9.dp),
            shape = RoundedCornerShape(8.dp),
            onClick = { openTagsList() }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
private fun SavedTagsList(
    savedTagsViewModel: SavedTagsViewModel,
    modifier: Modifier,
    tags: List<SavedTag>,
    openTag: (SavedTag) -> Unit
) {
    val uiState by savedTagsViewModel.uiState
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
                SavedTagView(item, openTag, savedTagsViewModel::removeSavedTag)
            }
        }
    }
}

@Composable
private fun SavedTagView(
    tag: SavedTag,
    openTag: (SavedTag) -> Unit,
    removeTag: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable { openTag(tag) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "#${tag.name}",
            modifier = Modifier.padding(start = 8.dp).width(160.dp),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (tag.hasNewEntries()) {
                val text = when {
                    tag.newEntriesCount < 25 -> "${tag.newEntriesCount} new entries"
                    else -> "more than ${tag.newEntriesCount} new entries"
                }
                Text(
                    text = text,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W700
                )
            }
            Icon(
                modifier = Modifier
                    .width(32.dp)
                    .clickable { removeTag(tag.name) },
                imageVector = Icons.Default.Favorite,
                contentDescription = "favourite",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview
@Composable
private fun TagViewPreview() {
    DiggTheme {
        SavedTagView(tag = SavedTag("motoryzacja", ""), { }, { })
    }
}
