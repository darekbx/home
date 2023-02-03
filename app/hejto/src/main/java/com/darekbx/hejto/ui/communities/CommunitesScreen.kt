package com.darekbx.hejto.ui.communities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.darekbx.hejto.data.remote.Community
import com.darekbx.hejto.ui.communities.viewmodel.CommunitesViewModel
import com.darekbx.hejto.ui.communities.viewmodel.UiState
import com.darekbx.hejto.ui.posts.*

@Composable
fun CommunitesScreen(
    communitesViewModel: CommunitesViewModel = hiltViewModel(),
    openCommunity: (String) -> Unit
) {
    val communities = communitesViewModel.communities.collectAsLazyPagingItems()
    val state by communitesViewModel.uiState

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LazyColumn(
            modifier = Modifier
                .padding(top = 4.dp, bottom = 4.dp)
        ) {
            items(items = communities) { item ->
                item?.let {
                    CommunityView(item) {
                        communitesViewModel.updateCommunityInfo(item.slug, item.postsCount)
                        openCommunity(item.slug)
                    }
                }
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
private fun CommunityView(community: Community, openCommunity: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable { openCommunity() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AuthorAvatar(community.avatar)
        Column {
            AuthorName(community.name)
            Spacer(modifier = Modifier.height(4.dp))
            if (community.previousPostsCount == community.postsCount) {
                CommentDate("Posts: ${community.postsCount}")
            } else {
                CommentDate(
                    "New posts: ${community.postsCount - community.previousPostsCount}",
                    FontWeight.W700,
                    MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
