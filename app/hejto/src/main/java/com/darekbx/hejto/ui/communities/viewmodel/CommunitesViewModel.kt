package com.darekbx.hejto.ui.communities.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.darekbx.hejto.data.CommonPagingSource
import com.darekbx.hejto.data.HejtoRespoitory
import com.darekbx.hejto.data.remote.HejtoService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommunitesViewModel @Inject constructor(
    private val hejtoRespoitory: HejtoRespoitory
) : ViewModel() {

    val communities = Pager(PagingConfig(pageSize = HejtoService.PAGE_SIZE)) {
        CommonPagingSource { page ->
            hejtoRespoitory.getCommunities(page)
        }
    }.flow
}
