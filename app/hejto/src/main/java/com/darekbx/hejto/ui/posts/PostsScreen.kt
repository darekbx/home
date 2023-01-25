package com.darekbx.hejto.ui.posts

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.darekbx.hejto.data.remote.Author
import com.darekbx.hejto.data.remote.Image
import com.darekbx.hejto.data.remote.PostDetails
import com.darekbx.hejto.data.remote.Tag
import com.darekbx.hejto.ui.HejtoTheme
import com.darekbx.hejto.ui.LinkedText
import com.darekbx.hejto.ui.posts.viemodel.Order
import com.darekbx.hejto.ui.posts.viemodel.PeriodFilter
import com.darekbx.hejto.ui.posts.viemodel.PostsViewModel

@Composable
fun PostsScreen(postsViewModel: PostsViewModel = hiltViewModel()) {
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
            PostsList(postsViewModel)
        }
    }
}

@Composable
private fun PostsList(postsViewModel: PostsViewModel) {
    val posts = postsViewModel.posts.collectAsLazyPagingItems()

    when (posts.loadState.refresh) {
        LoadState.Loading -> {
            //TODO implement loading state
            Log.v("--------", "Loading...")
        }
        is LoadState.Error -> {
            //TODO implement error state
            Log.v("--------", "Error")
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = posts, key = { it.slug }) { item ->
                    item?.let {
                        PostView(it)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PostViewPreview() {
    HejtoTheme {
        PostView()
    }
}

@Composable
private fun PostView(post: PostDetails = MockData.POST) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 4.dp, top = 4.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(4.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(4.dp)
                )
                .padding(8.dp)
        ) {
            Text(text = post.author.userName, modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary)
        }
        LinkedText(
            modifier = Modifier
                .padding(8.dp),
            content = post.cleanContent.replace("\\", "").replace("\\n\\n", "\\n"),
            links = post.links,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

object MockData {
    val POST = PostDetails(
        "article",
        "Zarobki inżynierów",
        "zarobki-inzynierow",
        "Na FB istnieje spora grupa zrzeszająca inżynierów budownictwa. Co roku wypuszczana jest tam anonimowa ankieta odnośnie zarobków. Co prawda ankieta ruszyła jakiś czas temu, ale może ktoś jeszcze zechce wypełnić. Daje ona pogląd przy rozmowach z pracodawcą czego można oczekiwać. \n\n [#budownictwo](/tag/budownictwo)",
        false,
        listOf(
            Image(urls = mapOf("500x500" to "https://hejto-media.s3.e…35a112e5faaed57114a2.jpg"))
        ),
        tags = listOf(Tag("pieniadze"), Tag("budownictwo")),
        author = Author(
            "MatiPuchacz",
            "Kompan",
            "#7c5292",
            Image(mapOf(
                "100x100" to "https://hejto-media.s3.eu-central-1.amazonaws.com/uploads/users/images/backgrounds/400x300/85b28c95ae9af101ee12d8e0dcc856a3.jpg",
            ))
        ),
        nsfw = false,
        controversial = false,
        likesCount = 7,
        commentsCount = 3,
        createdAt = "2023-01-20T20:21:18+01:00"
    )
}