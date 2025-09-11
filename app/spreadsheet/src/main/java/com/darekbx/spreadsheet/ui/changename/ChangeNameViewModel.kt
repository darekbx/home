package com.darekbx.spreadsheet.ui.changename

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.spreadsheet.domain.SpreadSheetUseCases
import com.darekbx.spreadsheet.model.SpreadSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeNameViewModel @Inject constructor(private val spreadSheetUseCases: SpreadSheetUseCases) :
    ViewModel() {

    suspend fun fetchSheet(uid: String) = spreadSheetUseCases.fetchSheet(uid)

    fun update(spreadSheet: SpreadSheet, onComplete: () -> Unit) {
        viewModelScope.launch {
            spreadSheetUseCases.updateSheetName(spreadSheet)
            onComplete()
        }
    }
}
