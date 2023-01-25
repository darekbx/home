package com.darekbx.hejto

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.darekbx.common.ui.NoInternetView
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.common.utils.ConnectionUtils
import com.darekbx.hejto.data.remote.Tag
import com.darekbx.hejto.ui.HejtoTheme
import com.darekbx.hejto.ui.LinkedText
import com.darekbx.hejto.ui.communitycategories.CommunityCategoriesViewModel
import com.darekbx.hejto.ui.posts.PostsScreen
import com.darekbx.hejto.utils.LinkParser
import dagger.hilt.android.AndroidEntryPoint

/**
 * Screens:
 * - MainScreen
 *  - Display posts
 *      - With filter on top
 *          - period: (all|6h|12h|24h|week|month|year)
 *          - type: (discussion|link|offer|article)
 *      - Filter is persisted between sessions
 *  - Menu:
 *      - Communities
 *      - Tags
 *      - Settings
 * - Communities (favourite communities)
 *   - list of the communities like in settings but with new posts counter
 * - Community
 *   - list of community posts with comment count and others
 *   - each entry can be opened to view details and comments
 * - Tags
 *   - list of the tags with new posts counter
 * - Tag
 *   - list of tag posts with comment count and others
 *   - each entry can be opened to view details and comments
 *
 * - Settings
 *  - Communities (list with communities, and filtering, ability to mark favourite communities)
 *  - Tags (list with tags, and filtering, ability to mark favourite tags)
 */
@AndroidEntryPoint
class HejtoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HejtoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (ConnectionUtils.isInternetConnected(LocalContext.current)) {

                        // TODO use navigation
                        PostsScreen()

                    } else {
                        NoInternetView(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
fun Test(communityCategoriesViewModel: CommunityCategoriesViewModel = hiltViewModel()) {

    val a = communityCategoriesViewModel.test2().collectAsState(initial = null)

    if (a != null) {
        val c = a.value?.slug
        Text(text = "${a.value?.images}")
    }
    //val data = communityCategoriesViewModel.test().collectAsLazyPagingItems()
    //MainScreen(data)
}

@Composable
fun MainScreen(books: LazyPagingItems<Tag>, modifier: Modifier = Modifier) {
    when (books.loadState.refresh) {
        LoadState.Loading -> {
            //TODO implement loading state
            Log.v("--------", "Loading...")
        }
        is LoadState.Error -> {
            //TODO implement error state
            Log.v("--------", "Error")
        }
        else -> {
            LazyColumn(modifier = modifier) {
                itemsIndexed(books) { index, item ->
                    item?.let {
                        Text(text = "${item.name}, posts: ${item.statistics.postsCount}")
                    }
                }
            }
        }
    }
}
