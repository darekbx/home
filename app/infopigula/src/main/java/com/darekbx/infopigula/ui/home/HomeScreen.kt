package com.darekbx.infopigula.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.InformationDialog
import com.darekbx.common.ui.isScrolledToEnd
import com.darekbx.infopigula.domain.GetNewsUseCase
import com.darekbx.infopigula.model.Group
import com.darekbx.infopigula.model.LastRelease
import com.darekbx.infopigula.model.News
import com.darekbx.infopigula.ui.theme.InfoPigulaTheme
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val newsStream = homeViewModel.news
    val groups = homeViewModel.groups
    val lastReleases = homeViewModel.lastReleases

    var page by remember { mutableIntStateOf(0) }
    val state = rememberLazyListState()
    val isAtBottom by remember { derivedStateOf { state.isScrolledToEnd() } }
    val uiState by homeViewModel.uiState.collectAsState(initial = HomeUiState.Idle)
    var errorDialogVisible by remember { mutableStateOf(false) }

    var activeGroup by remember { mutableIntStateOf(GetNewsUseCase.DEFAULT_GROUP) }
    var activeReleaseId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(activeGroup, activeReleaseId) {
        page = 0
        homeViewModel.clear()
        homeViewModel.loadNews(activeGroup, page, activeReleaseId)
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom && homeViewModel.hasNextPage) {
            page += 1
            homeViewModel.loadNews(activeGroup, page, activeReleaseId)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        Column(Modifier.fillMaxSize()) {
            lastReleases
                .firstOrNull {
                    it.targetId == (activeReleaseId
                        ?: lastReleases.firstOrNull()?.targetId)
                }
                ?.let { lastRelease ->
                    Text(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        text = lastRelease.date,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            GroupsRow(
                Modifier.fillMaxWidth(),
                groups,
                lastReleases,
                activeGroup,
                openGroup = { groupId -> activeGroup = groupId },
                releaseClicked = { releaseId -> activeReleaseId = releaseId }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F),
                contentAlignment = Alignment.TopStart
            ) {
                LazyColumn(state = state) {
                    itemsIndexed(items = newsStream) { index, item ->
                        NewsItem(news = item, index = index)
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                HomeUiState.InProgress -> LoadingProgress()
                is HomeUiState.Failed -> errorDialogVisible = true
                HomeUiState.Idle -> {}
                HomeUiState.Done -> {}
            }

            if (errorDialogVisible) {
                val failedState = uiState as HomeUiState.Failed
                InformationDialog(message = "Failed to fetch data! (${failedState.message})") {
                    homeViewModel.resetState()
                    errorDialogVisible = false
                }
            }

            if (newsStream.isEmpty() && uiState is HomeUiState.Done) {
                Text(
                    text = "Nothing to show...",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }

}

@Composable
fun NewsItem(news: News, index: Int) {
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        MarkdownText(
            modifier = Modifier
                .padding(16.dp),
            markdown = news.content,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp).width(80.dp),
                text = "#${index + 1}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            MarkdownText(
                markdown = news.sourceLogo
            )

            Row(
                modifier = Modifier.padding(end = 16.dp).width(80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = "star",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${news.voteScore} (${news.voteCount})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Divider()
    }
}

@Composable
fun GroupsRow(
    modifier: Modifier = Modifier,
    groups: List<Group>,
    lastReleases: List<LastRelease>,
    selectedGroupId: Int,
    openGroup: (Int) -> Unit = { },
    releaseClicked: (Int) -> Unit = { }
) {
    Row(modifier = modifier) {
        LazyRow(
            Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(start = 4.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
        ) {
            items(groups) { group ->
                GroupItem(
                    Modifier.clickable { openGroup(group.targetId) },
                    group = group,
                    selected = selectedGroupId == group.targetId
                )
            }
        }
        LatestReleases(Modifier, lastReleases, releaseClicked)
    }
}

@Composable
fun GroupItem(modifier: Modifier = Modifier, group: Group, selected: Boolean) {
    Row {
        Text(
            modifier = modifier.padding(4.dp),
            text = group.value,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun LatestReleases(
    modifier: Modifier = Modifier,
    lastReleases: List<LastRelease>,
    releaseClicked: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            lastReleases.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.date) },
                    onClick = {
                        expanded = false
                        releaseClicked(item.targetId)
                    }
                )
            }
        }
    }
}

@Composable
fun LoadingProgress() {
    CircularProgressIndicator(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            .padding(8.dp)
    )
}

@Preview
@Composable
fun HomeScreenPreview() {
    InfoPigulaTheme(isDarkTheme = false) {
        Surface {


        }
    }
}
