package com.darekbx.hejto.ui.communitycategories

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.darekbx.hejto.data.HejtoRespoitory
import com.darekbx.hejto.data.CommonPagingSource
import com.darekbx.hejto.data.remote.HejtoService.Companion.PAGE_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class CommunityCategoriesViewModel @Inject constructor(
    private val hejtoRespoitory: HejtoRespoitory
) : ViewModel() {

    // try/catch
    fun test() = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        CommonPagingSource { page ->
            hejtoRespoitory.getTags(page)
        }
    }.flow

    fun test2() = flow {
        emit(hejtoRespoitory.getPostDetails("no-leopard-tanks-for-ukraine-as-nato-allies-fail-to-agree"))
    }
}
