package com.darekbx.hejto.ui.saved.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.hejto.data.HejtoRespoitory
import com.darekbx.hejto.data.local.model.SavedSlug
import com.darekbx.hejto.data.remote.PostDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val hejtoRespoitory: HejtoRespoitory
) : ViewModel() {

    var listStateHolder = mutableStateOf(0.0)

    val savedSlugs = flow { emit(hejtoRespoitory.getSavedSlugs()) }

    fun addSlug(postDetails: PostDetails) {
        viewModelScope.launch {
            hejtoRespoitory.saveSlug(
                SavedSlug(
                    postDetails.slug,
                    postDetails.title,
                    postDetails.content
                )
            )
        }
    }

    fun removeSlug(slug: String) {
        viewModelScope.launch {
            hejtoRespoitory.removeSlug(slug)
            listStateHolder.value = Random.nextDouble()
        }
    }
}
