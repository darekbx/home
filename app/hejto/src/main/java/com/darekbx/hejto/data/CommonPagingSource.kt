package com.darekbx.hejto.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.darekbx.hejto.BuildConfig
import com.darekbx.hejto.data.remote.ResponseWrapper

class CommonPagingSource<T : Any>(
    private val source: suspend (page: Int) -> ResponseWrapper<T>
) : PagingSource<Int, T>() {

    override val keyReuseSupported: Boolean = true

    override fun getRefreshKey(state: PagingState<Int, T>): Int =
        ((state.anchorPosition ?: 0) - state.config.initialLoadSize / 2)
            .coerceAtLeast(0)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val currentPage = params.key ?: 1
            val response = source(currentPage)
            val nextKey = when (response.page >= response.pages) {
                true -> null // null is saying that there's nothing to load
                else -> response.page + 1
            }
            LoadResult.Page(
                data = response.contents.items,
                prevKey = null, // don't use backward paging, it is only for infinite scrolling
                nextKey = nextKey
            )
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            LoadResult.Error(e)
        }
    }
}
