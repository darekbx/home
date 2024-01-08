package com.darekbx.diggpl.ui.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.isScrolledToEnd
import com.darekbx.diggpl.WebViewActivity
import com.darekbx.diggpl.data.remote.Comment
import com.darekbx.diggpl.data.remote.MediaPhoto
import com.darekbx.diggpl.ui.*
import com.darekbx.diggpl.ui.homepage.UiState

@Composable
fun CommentsLazyList(
    modifier: Modifier = Modifier,
    linkId: Int? = null,
    entryId: Int? = null,
    header: @Composable () -> Unit = { },
    commentsViewModel: CommentsViewModel = hiltViewModel()
) {
    var page by remember { mutableIntStateOf(1) }
    val state = rememberLazyListState()
    val viewedIds = remember { mutableSetOf<Int>() }
    var viewedCount by remember { mutableIntStateOf(0) }
    val isAtBottom by remember { derivedStateOf { state.isScrolledToEnd() } }
    val uiState by commentsViewModel.uiState

    LaunchedEffect(Unit) {
        commentsViewModel.loadComments(linkId, entryId, page)
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom && commentsViewModel.hasNextPage) {
            page += 1
            commentsViewModel.loadComments(linkId, entryId, page)
        }
    }

    Box(modifier = modifier.fillMaxSize().padding(4.dp), contentAlignment = Alignment.Center) {
        LazyColumn(state = state) {
            item { header() }
            items(items = commentsViewModel.commentItems) { item ->
                viewedIds.add(item.id)
                viewedCount = viewedIds.size
                CommentView(item)
            }
        }
        when (uiState) {
            is UiState.InProgress -> LoadingProgress()
            is UiState.Error -> ErrorMessage((uiState as UiState.Error).message)
            is UiState.Idle -> { /* Do nothing */ }
        }

        ItemsViewedCount(viewedCount)
    }
}

@Composable
fun CommentView(comment: Comment) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // green, orange, burgundy
            AuthorView(comment.author)
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = comment.author.username,
                    color = comment.author.validColor(),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = comment.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        MarkdownContent(comment.content)
        Spacer(modifier = Modifier.height(8.dp))
        comment.media.survey?.let {
            SurveyView(survey = it)
        }
        Spacer(modifier = Modifier.height(8.dp))
        comment.media.photo?.let {
            CommonImage(it, comment.adult) {
                WebViewActivity.openImage(context, it.url)
            }
        }
        comment.media.embed
            ?.takeIf { it.thumbnail != null }
            ?.let {
                Box(contentAlignment = Alignment.BottomCenter) {
                    CommonImage(MediaPhoto("", it.thumbnail!!, ""), comment.adult) {
                        WebViewActivity.openImage(context, it.url!!)
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.onBackground)
                            .padding(4.dp),
                        text = it.url!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
    }
}