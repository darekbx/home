package com.darekbx.hejto.ui.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.darekbx.hejto.data.remote.*
import com.darekbx.hejto.ui.HejtoTheme
import com.darekbx.hejto.ui.posts.viemodel.Order
import com.darekbx.hejto.ui.posts.viemodel.PeriodFilter
import com.darekbx.hejto.ui.posts.viemodel.PostsViewModel
import com.darekbx.hejto.ui.posts.viemodel.UiState

fun LazyListState.isScrolledToEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@Composable
fun PostsScreen(
    postsViewModel: PostsViewModel = hiltViewModel(),
    tag: String,
    openPost: (slug: String) -> Unit = { }
) {
    var page by remember { mutableStateOf(1) }
    val posts = postsViewModel.postsList
    val state = rememberLazyListState()
    val isAtBottom = state.isScrolledToEnd()
    val uiState by postsViewModel.uiState

    LaunchedEffect(tag) {
        postsViewModel.loadPosts(tag, page)
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom && postsViewModel.hasNextPage) {
            page += 1
            postsViewModel.loadPosts(tag, page)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LazyColumn(state = state) {
            items(items = posts) { item ->
                PostView(item, openPost = openPost)
            }
        }
        when (uiState) {
            is UiState.InProgress -> LoadingProgress()
            is UiState.Error -> ErrorMessage((uiState as UiState.Error).message)
            is UiState.Idle -> { /* Do nothing */ }
        }
    }
}

// ---------------------- Jetpack Paging version

@Composable
fun PostsScreen(
    postsViewModel: PostsViewModel = hiltViewModel(),
    openPost: (slug: String) -> Unit = { }
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val activePeriod by postsViewModel.periodFilter.collectAsState(initial = PeriodFilter.`6H`)
        val activeOrder by postsViewModel.postsOrder.collectAsState(initial = Order.NEWEST)

        FilterView(
            activePeriod,
            activeOrder,
            onPeriodChanged = postsViewModel::periodChanged,
            onOrderChanged = postsViewModel::orderChanged
        )

        val postsStateHolder by remember { postsViewModel.postsStateHolder }
        key(postsStateHolder) {
            val posts = postsViewModel.posts.collectAsLazyPagingItems()
            PostsList(posts, openPost)
        }
    }
}

@Composable
private fun PostsList(
    posts: LazyPagingItems<PostDetails>,
    openPost: (slug: String) -> Unit = { }
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (posts.loadState.refresh) {
            is LoadState.Loading -> LoadingProgress()
            is LoadState.Error -> ErrorIcon()
            else -> {
                LazyColumn {
                    items(items = posts) { item ->
                        item?.let {
                            PostView(it, openPost)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PostView(
    post: PostDetails = MockData.POST,
    openPost: (slug: String) -> Unit = { }
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 4.dp, top = 4.dp)
            .background(
                MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PostHeader(post)
        PostContent(post)
        post.images.forEach { remoteImage ->
            CommonImage(remoteImage, post.nsfw)
        }
        PostFooter(post, openPost)
    }
}

@Composable
fun PostFooter(
    post: PostDetails,
    openPost: (slug: String) -> Unit = { }
) {
    Row(
        modifier = Modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.clickable { openPost(post.slug) },
            text = buildAnnotatedString {
                append("Comments: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W600)) {
                    append("${post.commentsCount}")
                }
            },
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.6.sp),
            color = MaterialTheme.colorScheme.onPrimary,
            textDecoration = if (post.commentsCount > 0) TextDecoration.Underline else TextDecoration.None
        )
        if (post.link != null) {
            val localUriHandler = LocalUriHandler.current
            Text(
                modifier = Modifier.clickable { localUriHandler.openUri(post.link) },
                text = "Open link",
                style = MaterialTheme.typography.titleSmall.copy(letterSpacing = 0.6.sp),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        }
        Text(
            text = post.displayDate(),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.6.sp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Preview
@Composable
private fun PostViewPreview() {
    HejtoTheme {
        PostView()
    }
}
