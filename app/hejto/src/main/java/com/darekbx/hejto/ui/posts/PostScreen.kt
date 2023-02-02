@file:OptIn(ExperimentalFoundationApi::class)

package com.darekbx.hejto.ui.posts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.darekbx.hejto.data.remote.*
import com.darekbx.hejto.ui.HejtoTheme
import com.darekbx.hejto.ui.posts.MockData.COMMENT
import com.darekbx.hejto.ui.posts.viemodel.PostsViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun PostScreen(
    slug: String,
    postsViewModel: PostsViewModel = hiltViewModel()
) {
    val comments = postsViewModel.postComments(slug).collectAsLazyPagingItems()
    val post by postsViewModel.post(slug).collectAsState(initial = null)
    post?.let {
        PostComments(post = it, comments = comments)
    }
}

@Composable
private fun PostComments(
    post: PostDetails,
    comments: LazyPagingItems<PostComment>
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (comments.loadState.refresh) {
            is LoadState.Loading -> LoadingProgress()
            is LoadState.Error -> ErrorIcon()
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        PostDetails(post)
                    }
                    items(items = comments) { item ->
                        item?.let {
                            CommentView(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PostDetails(post: PostDetails?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        post?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 4.dp, top = 8.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                PostHeader(it, onLongClick = { /* Do nothing for post view */ })
                PostContent(it.content)
                it.images.forEach { remoteImage ->
                    CommonImage(remoteImage, it.nsfw)
                }
            }
        } ?: run {
            LoadingProgress()
        }
    }
}

@Composable
private fun CommentView(
    comment: PostComment = COMMENT
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 4.dp, top = 4.dp)
            .background(
                MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)
            )
    ) {
        CommentHeader(comment)
        CommentContent(comment)
        comment.images.forEach { remoteImage ->
            CommonImage(remoteImage, isNsfw = false)
        }
        comment.contentLinks.forEach { contentLink ->
            ContentLinkView(contentLink, isNsfw = false)
        }
    }
}

@Composable
fun CommentHeader(postComment: PostComment) {
    val ago by remember {
        derivedStateOf { postComment.dateAgo() }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AuthorAvatar(postComment.author.avatar)
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuthorName(postComment.author.userName)
                    AuthorRank(postComment.author)
                }
                CommentDate(ago)
            }
        }
        PostLikesInfo(postComment.likesCount)
    }
}

@Composable
fun CommentDate(
    ago: String,
    weight: FontWeight = FontWeight.W200,
    color: Color = MaterialTheme.colorScheme.onPrimary
) {
    Text(
        text = ago,
        modifier = Modifier.padding(start = 8.dp),
        style = MaterialTheme.typography.titleSmall,
        color = color,
        fontWeight = weight
    )
}

@Composable
fun CommentContent(comment: PostComment) {
    MarkdownText(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, start = 48.dp, end = 8.dp),
        markdown = comment.content,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Preview
@Composable
private fun CommentViewPreview() {
    HejtoTheme {
        CommentView()
    }
}
