package com.darekbx.diggpl.ui.saved

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.diggpl.data.WykopRepository
import com.darekbx.diggpl.data.remote.ResourceType
import com.darekbx.diggpl.data.remote.StreamItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val wykopRepository: WykopRepository
) : ViewModel() {

    var listStateHolder = mutableStateOf(0.0)

    val savedSlugs = flow { emit(wykopRepository.getSavedItems()) }

    fun countSavedItems() = flow { emit(wykopRepository.countSavedItems()) }

    fun add(item: StreamItem) {
        viewModelScope.launch {
            when (item.resource) {
                ResourceType.ENTRY.type -> wykopRepository.saveEntry(item)
                ResourceType.LINK.type -> wykopRepository.saveLink(item)
            }
        }
    }

    fun removeLink(linkId: Int) {
        viewModelScope.launch {
            wykopRepository.removeLink(linkId)
            listStateHolder.value = Random.nextDouble()
        }
    }

    fun removeEntry(entryId: Int) {
        viewModelScope.launch {
            wykopRepository.removeEntry(entryId)
            listStateHolder.value = Random.nextDouble()
        }
    }
}
