@file:OptIn(ExperimentalTextApi::class, ExperimentalTextApi::class)

package com.darekbx.hejto.ui.posts.viemodel

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.darekbx.hejto.data.CommonPagingSource
import com.darekbx.hejto.data.HejtoRespoitory
import com.darekbx.hejto.data.remote.HejtoService
import com.darekbx.hejto.data.remote.PostDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@OptIn(ExperimentalTextApi::class)
@Preview(widthDp = 200, heightDp = 200)
@Composable
fun Bambilotto() {
    val textMeasure = rememberTextMeasurer()
    Box(modifier = Modifier.fillMaxSize(), Alignment.BottomCenter) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            drawCircle(color = Color.Black, radius = size.minDimension / 3.0f, style = Stroke(4.0f))
            translate(-12F, 4F) {
                // B
                scale(0.8F, 1F) {
                    drawText(
                        textMeasurer = textMeasure,
                        text = "B",
                        topLeft = Offset(264F, 72F),
                        style = TextStyle(fontSize = 104.sp, fontWeight = FontWeight.W100)
                    )
                }
                // A
                drawLine(
                    Color.Black,
                    strokeWidth = 8F,
                    start = Offset(220F, 372F),
                    end = Offset(291F, 174F)
                )
                drawLine(
                    Color.Black,
                    strokeWidth = 8F,
                    start = Offset(290F, 300F),
                    end = Offset(244F, 300F)
                )
                // L
                drawLine(
                    Color.Black,
                    strokeWidth = 8F,
                    start = Offset(200F, 176F),
                    end = Offset(200F, 372F)
                )
                drawLine(
                    Color.Black,
                    strokeWidth = 8F,
                    start = Offset(197F, 369F),
                    end = Offset(222F, 369F)
                )
                // M
                drawLine(
                    Color.Black,
                    strokeWidth = 8F,
                    start = Offset(201F, 178F),
                    end = Offset(255F, 270F)
                )
                //_
                drawLine(
                    Color.Black,
                    strokeWidth = 8F,
                    start = Offset(180F, 176F),
                    end = Offset(222F, 176F)
                )
                // .
                drawCircle(Color.Black, radius = 12F, center = Offset(291F, 144F))
            }
        }
    }
}

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val hejtoRespoitory: HejtoRespoitory,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    var postsStateHolder = mutableStateOf(0.0)

    /*
    TODO try to refactor to UIState

sealed class UiState {
    object InProgress : UiState()
    object Idle : UiState()
}

     */

    var postsList = mutableStateListOf<PostDetails>()
    var isLoading = mutableStateOf(false)
    var hasNextPage = true

    fun loadPosts(tag: String?, page: Int) {
        viewModelScope.launch {
            isLoading.value = true

            val data = hejtoRespoitory.getPosts(
                page = page,
                tag = tag,
                PeriodFilter.ALL,
                Order.NEWEST
            )

            hasNextPage = data.page < data.pages

            isLoading.value = false
            postsList.addAll(data.contents.items)
        }
    }

    val posts =
        Pager(PagingConfig(pageSize = HejtoService.PAGE_SIZE)) {
            CommonPagingSource { page ->
                Log.v("--------", "Load page: $page")
                hejtoRespoitory.getPosts(
                    page = page,
                    tag = null,
                    periodFilter.first(),
                    postsOrder.first()
                )
            }
        }.flow

    fun post(slug: String) = flow {
        emit(hejtoRespoitory.getPostDetails(slug))
    }

    fun postComments(slug: String) =
        Pager(PagingConfig(pageSize = HejtoService.PAGE_SIZE)) {
            CommonPagingSource { page ->
                hejtoRespoitory.getPostComments(page, slug)
            }
        }.flow

    val periodFilter = dataStore.data.map { preferences ->
        val value = preferences[PERIOD_FILTER]
        PeriodFilter.values()[value ?: DEFAULT_FILTER_INDEX]
    }

    val postsOrder = dataStore.data.map { preferences ->
        val value = preferences[POSTS_ORDER]
        Order.values()[value ?: DEFAULT_ORDER_INDEX]
    }

    fun periodChanged(periodFilter: PeriodFilter) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PERIOD_FILTER] = periodFilter.ordinal
            }
            invokePostsRefresh()
        }
    }

    fun orderChanged(order: Order) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[POSTS_ORDER] = order.ordinal
            }
            invokePostsRefresh()
        }
    }

    private fun invokePostsRefresh() {
        postsStateHolder.value = Random.nextDouble()
    }

    companion object {
        private val PERIOD_FILTER = intPreferencesKey("period_filter")
        private val POSTS_ORDER = intPreferencesKey("posts_order")

        private const val DEFAULT_FILTER_INDEX = 2 // 24h
        private const val DEFAULT_ORDER_INDEX = 0 // Newest
    }
}
