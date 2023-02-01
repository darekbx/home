@file:OptIn(ExperimentalFoundationApi::class)

package com.darekbx.hejto.ui.saved

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.hejto.data.local.model.SavedSlug
import com.darekbx.hejto.ui.posts.*
import com.darekbx.hejto.ui.saved.viewmodel.SavedViewModel

@Composable
fun SavedScreen(
    savedViewModel: SavedViewModel = hiltViewModel(),
    openPost: (slug: String) -> Unit = { }
) {
    val listStateHolder by remember { savedViewModel.listStateHolder }
    key(listStateHolder) {
        val savedSlugs by savedViewModel.savedSlugs.collectAsState(initial = null)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                savedSlugs == null -> LoadingProgress()
                savedSlugs?.isEmpty() == true -> EmptyMessage()
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 4.dp, bottom = 4.dp)
                    ) {
                        items(items = savedSlugs ?: emptyList()) { item ->
                            SavedSlugView(item, openPost, removeSlug = savedViewModel::removeSlug)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedSlugView(
    savedSlug: SavedSlug,
    openSlug: (slug: String) -> Unit = { },
    removeSlug: (slug: String) -> Unit = { }
) {
    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .combinedClickable(
                onClick = { openSlug(savedSlug.slug) },
                onLongClick = { removeSlug(savedSlug.slug) }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Column {
            AuthorName(savedSlug.title)
            Spacer(modifier = Modifier.height(4.dp))
            PostContent(savedSlug.contents)
        }
    }
}

@Composable
fun EmptyMessage() {
    Text(
        text = "Nothing is saved",
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleMedium
    )
}
