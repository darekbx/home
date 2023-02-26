package com.darekbx.diggpl.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.diggpl.R
import com.darekbx.diggpl.data.local.model.SavedEntry
import com.darekbx.diggpl.data.local.model.SavedLink
import com.darekbx.diggpl.ui.LinkTitle
import com.darekbx.diggpl.ui.LoadingProgress
import com.darekbx.diggpl.ui.MarkdownContent

@Composable
fun SavedItemsScreen(
    savedViewModel: SavedViewModel = hiltViewModel(),
    openLink: (linkId: Int) -> Unit = { },
    openEntry: (entryId: Int) -> Unit = { }
) {
    val listStateHolder by remember { savedViewModel.listStateHolder }
    key(listStateHolder) {
        val savedItems by savedViewModel.savedSlugs.collectAsState(initial = null)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                savedItems == null -> LoadingProgress()
                savedItems?.isEmpty() == true -> EmptyMessage()
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 4.dp, bottom = 4.dp)
                    ) {
                        items(items = savedItems ?: emptyList()) { item ->
                            when (item) {
                                is SavedEntry -> {
                                    SavedEntryView(
                                        item,
                                        { openEntry(item.entryId) },
                                        { savedViewModel.removeEntry(item.entryId) }
                                    )
                                }
                                is SavedLink -> {
                                    SavedLinkView(
                                        item,
                                        { openLink(item.linkId) },
                                        { savedViewModel.removeLink(item.linkId) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedLinkView(
    savedLink: SavedLink,
    openLink: () -> Unit = { },
    removeLink: () -> Unit = { }
) {
    Column(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
            .padding(8.dp)

    ) {
        LinkTitle(savedLink.title, source = null)
        Spacer(modifier = Modifier.height(4.dp))
        MarkdownContent(savedLink.contents)
        Spacer(modifier = Modifier.height(4.dp))
        ActionsRow(openLink, removeLink)
    }
}

@Composable
fun SavedEntryView(
    savedEntry: SavedEntry,
    openEntry: () -> Unit = { },
    removeEntry: () -> Unit = { }
) {
    Column(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        MarkdownContent(savedEntry.contents)
        Spacer(modifier = Modifier.height(4.dp))
        ActionsRow(openEntry, removeEntry)
    }
}

@Composable
private fun ActionsRow(open: () -> Unit, remove: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .clickable { open() },
            painter = painterResource(id = R.drawable.ic_open),
            contentDescription = "open",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .clickable { remove() },
            painter = painterResource(id = R.drawable.ic_delete),
            contentDescription = "delete",
            tint = MaterialTheme.colorScheme.primary
        )
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
