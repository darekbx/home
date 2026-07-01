package com.darekbx.infopigula.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_6A
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.InformationDialog
import com.darekbx.infopigula.model.Category
import com.darekbx.infopigula.model.News
import com.darekbx.infopigula.model.NewsResponse
import com.darekbx.infopigula.model.Source
import com.darekbx.infopigula.ui.theme.InfoPigulaTheme
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState(initial = HomeUiState.Idle)
    var errorDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        homeViewModel.loadNews()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is HomeUiState.Failed -> errorDialogVisible = true
            is HomeUiState.Done -> News(state.data)
            HomeUiState.InProgress -> LoadingProgress()
            HomeUiState.Idle -> {}
        }

        if (errorDialogVisible) {
            val failedState = uiState as HomeUiState.Failed
            InformationDialog(message = "Failed to fetch data! (${failedState.message})") {
                homeViewModel.resetState()
                errorDialogVisible = false
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun News(data: NewsResponse) {
    val items = data.categories
    Column {
        Text(
            text = data.title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(items) { item ->
                NewsItem(item)
            }
        }
    }
}

@Composable
fun NewsItem(category: Category) {
    Column {
        GroupItem(name = category.name)
        category.news.forEachIndexed { index, news ->
            val corners = if (index >= category.news.size - 1) {
                RoundedCornerShape(0.dp, 0.dp, 12.dp, 12.dp)
            } else {
                RoundedCornerShape(0.dp)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, bottom = 1.dp)
                    .background(Color(0xFF1A1A1A), corners)
            ) {
                MarkdownText(
                    modifier = Modifier
                        .padding(8.dp),
                    markdown = news.content,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFE0E0E0)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MarkdownText(
                        markdown = news.source.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${news.rating} (${news.totalVotes})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun GroupItem(modifier: Modifier = Modifier, name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .padding(start = 4.dp, end = 4.dp, bottom = 1.dp)
            .background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp)),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = modifier.padding(4.dp),
            text = name,
            fontSize = 16.sp,
            color = Color(0xFFEEEEEE),
            fontWeight = FontWeight.Normal
        )
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

@Preview(device = PIXEL_6A, showBackground = true, showSystemUi = true)
@Composable
fun NewsPreview() {
    InfoPigulaTheme(isDarkTheme = true) {
        Surface() {
            News(
                data = NewsResponse(
                    title = "Wydanie 13.04.1922",
                    categories = listOf(
                        Category(
                            name = "Polska",
                            news = listOf(
                                News(
                                    id = "1",
                                    content = "<p><strong style=\\\"background-color: transparent; color: rgb(0, 0, 0);\\\">Były premier Morawiecki oddaje ukraiński order do Muzeum Pamięci Ofiar Rzezi Wołyńskiej w Chełmie jako dowód pamięci o ofiarach UPA i sprzeciw wobec polityki Kijowa. Nazwał ukraińskie władze „skorumpowaną elitą”, która nie potrafi uszanować polskiej historii.</strong></p>",
                                    images = listOf(),
                                    rating = 4.5,
                                    totalVotes = 412,
                                    source = Source(
                                        image = "https://infopigula.pl/zdjecia/zrodlo/Polsat_News_2021_gradient.svg_.png",
                                        name = "Polsat News",
                                        url = "https://www.polsatnews.pl/wiadomosc/2026-06-30/morawiecki-oddaje-order-skorumpowana-elita-ukrainska/"
                                    )
                                ),
                                News(
                                    id = "2",
                                    content = "<p><strong style=\\\"background-color: transparent; color: rgb(0, 0, 0);\\\">Były premier Morawiecki oddaje ukraiński order do Muzeum Pamięci Ofiar Rzezi Wołyńskiej w Chełmie jako dowód pamięci o ofiarach UPA i sprzeciw wobec polityki Kijowa. Nazwał ukraińskie władze „skorumpowaną elitą”, która nie potrafi uszanować polskiej historii.</strong></p>",
                                    images = listOf(),
                                    rating = 4.5,
                                    totalVotes = 412,
                                    source = Source(
                                        image = "https://infopigula.pl/zdjecia/zrodlo/Polsat_News_2021_gradient.svg_.png",
                                        name = "Polsat News",
                                        url = "https://www.polsatnews.pl/wiadomosc/2026-06-30/morawiecki-oddaje-order-skorumpowana-elita-ukrainska/"
                                    )
                                )
                            )
                        ),
                        Category(
                            name = "Świat",
                            news = listOf(
                                News(
                                    id = "1",
                                    content = "<p><strong style=\\\"background-color: transparent; color: rgb(0, 0, 0);\\\">Były premier Morawiecki oddaje ukraiński order do Muzeum Pamięci Ofiar Rzezi Wołyńskiej w Chełmie jako dowód pamięci o ofiarach UPA i sprzeciw wobec polityki Kijowa. Nazwał ukraińskie władze „skorumpowaną elitą”, która nie potrafi uszanować polskiej historii.</strong></p>",
                                    images = listOf(),
                                    rating = 4.5,
                                    totalVotes = 412,
                                    source = Source(
                                        image = "https://infopigula.pl/zdjecia/zrodlo/Polsat_News_2021_gradient.svg_.png",
                                        name = "Polsat News",
                                        url = "https://www.polsatnews.pl/wiadomosc/2026-06-30/morawiecki-oddaje-order-skorumpowana-elita-ukrainska/"
                                    )
                                ),
                                News(
                                    id = "2",
                                    content = "<p><strong style=\\\"background-color: transparent; color: rgb(0, 0, 0);\\\">Były premier Morawiecki oddaje ukraiński order do Muzeum Pamięci Ofiar Rzezi Wołyńskiej w Chełmie jako dowód pamięci o ofiarach UPA i sprzeciw wobec polityki Kijowa. Nazwał ukraińskie władze „skorumpowaną elitą”, która nie potrafi uszanować polskiej historii.</strong></p>",
                                    images = listOf(),
                                    rating = 4.5,
                                    totalVotes = 412,
                                    source = Source(
                                        image = "https://infopigula.pl/zdjecia/zrodlo/Polsat_News_2021_gradient.svg_.png",
                                        name = "Polsat News",
                                        url = "https://www.polsatnews.pl/wiadomosc/2026-06-30/morawiecki-oddaje-order-skorumpowana-elita-ukrainska/"
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }
    }
}