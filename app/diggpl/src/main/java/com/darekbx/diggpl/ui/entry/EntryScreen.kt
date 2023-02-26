package com.darekbx.diggpl.ui.entry

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.diggpl.data.remote.*
import com.darekbx.diggpl.ui.*
import com.darekbx.diggpl.ui.comments.CommentsLazyList

@Composable
fun EntryScreen(
    entryId: Int,
    modifier: Modifier = Modifier,
    entryViewModel: EntryViewModel = hiltViewModel()
) {
    val entry by entryViewModel.loadEntry(entryId).collectAsState(initial = null)

    CommentsLazyList(entryId = entryId, header = {
        EntryContentHeader(entry)
    })
}

@Composable
private fun EntryContentHeader(
    entry: ResponseResult<DataWrapper<StreamItem>>?,
) {
    Column {
        entry?.let { result ->
            when (result) {
                is ResponseResult.Success -> EntryContent(result.data.data)
                is ResponseResult.Failure -> ErrorMessage(
                    result.error.message ?: "Unknown error"
                )
            }
        } ?: LoadingView()

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun LoadingView() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) { LoadingProgress() }
}

@Composable
private fun EntryContent(entry: StreamItem) {
    Column(modifier = Modifier.padding(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AuthorView(entry.author)
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = entry.author.username,
                    color = entry.author.validColor(),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = entry.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        MarkdownContent(entry.content)
        Spacer(modifier = Modifier.height(8.dp))
        entry.media.survey?.let {
            SurveyView(survey = it)
        }
        LinkImages(entry)
        LinkSource(entry)
    }
}
