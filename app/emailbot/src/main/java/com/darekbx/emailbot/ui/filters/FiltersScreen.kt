package com.darekbx.emailbot.ui.filters

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.emailbot.model.SpamFilter
import com.darekbx.emailbot.ui.ErrorView
import com.darekbx.emailbot.ui.ProgressView
import com.darekbx.emailbot.ui.theme.EmailBotTheme

@Composable
fun FiltersScreen(viewModel: FiltersViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var filterToDelete by remember { mutableStateOf<SpamFilter?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchSpamFilters()
    }

    when (val state = uiState) {
        is FiltersUiState.Idle -> { /* NOOP */ }
        is FiltersUiState.Loading -> ProgressView()
        is FiltersUiState.Error -> ErrorView(state.e) { viewModel.fetchSpamFilters() }
        is FiltersUiState.Success -> SpamFiltersList(
            filters = state.emails,
            onLongClick = { filter ->
                filterToDelete = filter
            }
        )
    }

    filterToDelete?.let {
        DeleteConfirmationDialog(
            filter = it,
            onConfirm = {
                viewModel.deleteSpamFilter(it.id)
                filterToDelete = null
            },
            onDismiss = {
                filterToDelete = null
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SpamFiltersList(filters: List<SpamFilter>, onLongClick: (SpamFilter) -> Unit = { }) {
    LazyColumn(Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        itemsIndexed(items = filters) { index, filter ->
            val backgroundColor = if (index % 2 == 0) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
            SpamFilterItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(
                        color = backgroundColor,
                        shape = MaterialTheme.shapes.small
                    )
                    .combinedClickable(
                        onLongClick = { onLongClick(filter) },
                        onClick = { }
                    ),
                filter = filter,
            )
        }
    }
}


@Composable
private fun SpamFilterItem(modifier: Modifier, filter: SpamFilter) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1F)) {
            filter.from?.let {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                            )
                        ) { append("From: ") }
                        append(it)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            filter.subject?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
fun SpamFiltersListPreview() {
    EmailBotTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            SpamFiltersList(listOf(
                SpamFilter("1", "spam@server.com", null),
                SpamFilter("2", null, "Spam subject")
            ))
        }
    }
}
