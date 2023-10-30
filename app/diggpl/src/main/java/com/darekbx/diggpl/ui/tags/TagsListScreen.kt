@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.darekbx.diggpl.ui.tags

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.diggpl.ui.DiggTheme
import com.darekbx.diggpl.ui.ErrorIcon
import com.darekbx.diggpl.ui.LoadingProgress

data class Tag(val name: String) {
    var isFavourite = false
}

@Composable
fun TagsListScreen(savedTagsViewModel: SavedTagsViewModel = hiltViewModel()) {
    val favouriteTags by savedTagsViewModel.getSavedTags().collectAsState(initial = emptyList())
    val autocompleteTags = savedTagsViewModel.autocompleteTags

    var text by remember { mutableStateOf("") }
    val state by savedTagsViewModel.uiState

    LaunchedEffect(text) {
        if (text.length > 2) {
            savedTagsViewModel.tagAutocomplete(text)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp, bottom = 4.dp)
        ) {
            stickyHeader {
                TextField(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    value = text,
                    label = { Text("Enter tag name") },
                    onValueChange = { text = it },
                    shape = RoundedCornerShape(4.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    ),
                )
            }
            items(items = autocompleteTags) { item ->
                item.isFavourite = favouriteTags.any { it.name == item.name }
                TagView(item, savedTagsViewModel::addRemoveSavedTag)
            }
        }

        when (state) {
            is UiState.InProgress -> LoadingProgress()
            is UiState.Error -> ErrorIcon()
            else -> { /* Do nothing */ }
        }
    }
}

@Composable
private fun TagView(tag: Tag, onFavouriteClick: (name: String) -> Unit = { }) {
    var isFavourite by remember { mutableStateOf(tag.isFavourite) }
    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
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
        Icon(
            modifier = Modifier
                .width(32.dp)
                .clickable {
                    isFavourite = !isFavourite
                    onFavouriteClick(tag.name)
                },
            imageVector =
            if (isFavourite) Icons.Default.Favorite
            else Icons.Outlined.FavoriteBorder,
            contentDescription = "views",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Preview
@Composable
private fun TagViewPreview() {
    DiggTheme {
        TagView(tag = Tag("motoryzacja"))
    }
}
