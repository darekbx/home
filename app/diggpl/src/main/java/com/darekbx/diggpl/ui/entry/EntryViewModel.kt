package com.darekbx.diggpl.ui.entry

import androidx.lifecycle.ViewModel
import com.darekbx.diggpl.data.WykopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val wykopRepository: WykopRepository
) : ViewModel() {

    fun loadEntry(entryId: Int) = flow {
        emit(wykopRepository.getEntry(entryId))
    }

}
