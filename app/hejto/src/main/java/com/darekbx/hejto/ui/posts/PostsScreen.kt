package com.darekbx.hejto.ui.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.darekbx.hejto.data.remote.*
import com.darekbx.hejto.ui.HejtoTheme
import com.darekbx.hejto.ui.posts.viemodel.Order
import com.darekbx.hejto.ui.posts.viemodel.PeriodFilter
import com.darekbx.hejto.ui.posts.viemodel.PostsViewModel

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
            PostsList(postsViewModel, openPost = openPost)
        }
    }
}

@Composable
private fun PostsList(
    postsViewModel: PostsViewModel,
    openPost: (slug: String) -> Unit = { }
) {
    val posts = postsViewModel.posts.collectAsLazyPagingItems()
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        when (posts.loadState.refresh) {
            is LoadState.Loading -> LoadingProgress()
            is LoadState.Error -> ErrorIcon()
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = posts, key = { it.slug }) { item ->
                        item?.let {
                            PostView(it, openPost = openPost)
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
            )
    ) {
        PostHeader(post)
        PostContent(post, openPost)
        CommonImage(post.images)
        PostFooter(post)
    }
}

@Composable
fun PostFooter(post: PostDetails) {
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
            text = buildAnnotatedString {
                append("Komentarzy: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.W600)) {
                    append("${post.commentsCount}")
                }
            },
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.6.sp),
            color = MaterialTheme.colorScheme.onPrimary
        )
        if (post.link != null) {
            val localUriHandler = LocalUriHandler.current
            Text(
                modifier = Modifier.clickable {
                    localUriHandler.openUri(post.link)
                },
                text = "Otworz link",
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
