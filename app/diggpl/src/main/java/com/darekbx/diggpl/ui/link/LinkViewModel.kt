package com.darekbx.diggpl.ui.link

import androidx.lifecycle.ViewModel
import com.darekbx.diggpl.data.WykopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class LinkViewModel @Inject constructor(
    private val wykopRepository: WykopRepository
) : ViewModel() {

    fun loadLink(linkId: Int) = flow {
        emit(wykopRepository.getLink(linkId))
    }

    fun loadLinkRelated(linkId: Int) = flow {
        emit(wykopRepository.getLinkRelated(linkId))
    }
}
