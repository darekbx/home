package com.darekbx.hejto.ui.posts.viemodel

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.darekbx.hejto.data.CommonPagingSource
import com.darekbx.hejto.data.HejtoRespoitory
import com.darekbx.hejto.data.remote.HejtoService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val hejtoRespoitory: HejtoRespoitory,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    var postsStateHolder = mutableStateOf(0.0)

    var posts =
        Pager(PagingConfig(pageSize = HejtoService.PAGE_SIZE)) {
            CommonPagingSource { page ->
                hejtoRespoitory.getPosts(page, periodFilter.first(), postsOrder.first())
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

        private val DEFAULT_FILTER_INDEX = 2 // 24h
        private val DEFAULT_ORDER_INDEX = 0 // Newest
    }
}
