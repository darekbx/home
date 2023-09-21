package com.darekbx.geotracker.ui.home.recording

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel

class RecordingViewState(
    private val recordingViewModel: RecordingViewModel
) {
    val state: RecordingUiState
        @Composable get() = recordingViewModel.uiState
            .collectAsState(initial = RecordingUiState.Stopped).value

    fun setIsRecording() {
        recordingViewModel.setIsRecording()
    }

    fun stopRecording() {
        recordingViewModel.stopRecording()
    }

    suspend fun checkIsRecording() {
        recordingViewModel.checkIsRecording()
    }

    fun fetchAllTracks() = recordingViewModel.fetchAllTracks()

    fun loadGpx(uri: Uri?) = recordingViewModel.loadGpx(uri)
}

@Composable
fun rememberRecordingViewState(
    recordingViewModel: RecordingViewModel = hiltViewModel()
) = remember {
    RecordingViewState(recordingViewModel)
}