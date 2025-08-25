package com.darekbx.geotracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.domain.usecase.AddLocationUseCase
import com.darekbx.geotracker.domain.usecase.DeleteAndRestoreUseCase
import com.darekbx.geotracker.domain.usecase.GetCountPointsUseCase
import com.darekbx.geotracker.domain.usecase.SynchronizeUseCase
import com.darekbx.geotracker.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class SettingsUiState {
    data object Idle : SettingsUiState()
    data object InProgress : SettingsUiState()
    data class Done(
        val nthPointsToSkip: Int,
        val gpsMinDistance: Float,
        val gpsUpdateInterval: Long,
        val showYearSummaryValue: Boolean,
        val uploadLastLocation: Boolean
    ) :
        SettingsUiState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val deleteAndRestoreUseCase: DeleteAndRestoreUseCase,
    private val settingsRepository: SettingsRepository,
    private val synchronizeUseCase: SynchronizeUseCase,
    private val addLocationUseCase: AddLocationUseCase,
    private val getCountPointsUseCase: GetCountPointsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val uiState: Flow<SettingsUiState>
        get() = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.InProgress
            val nthPointsToSkip = settingsRepository.nthPointsToSkip()
            val gpsMinDistance = settingsRepository.gpsMinDistance()
            val gpsUpdateInterval = settingsRepository.gpsUpdateInterval()
            val showYearSummaryValue = settingsRepository.showYearSummary()
            val uploadLastLocation = settingsRepository.uploadLastLocation()

            _uiState.value =
                SettingsUiState.Done(
                    nthPointsToSkip,
                    gpsMinDistance,
                    gpsUpdateInterval,
                    showYearSummaryValue,
                    uploadLastLocation
                )
        }
    }

    fun save(
        nthPointsToSkip: Int,
        gpsMinDistance: Float,
        gpsUpdateInterval: Long,
        showYearSummaryValue: Boolean,
        uploadLastLocation: Boolean
    ) {
        viewModelScope.launch {
            settingsRepository.saveSettings(
                nthPointsToSkip,
                gpsMinDistance,
                gpsUpdateInterval,
                showYearSummaryValue,
                uploadLastLocation
            )

            refresh()
        }
    }

    fun getPointsCount(count: (Long) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                count(getCountPointsUseCase())
            }
        }
    }

    fun addManually(distance: Float, start: Long, end: Long) {
        viewModelScope.launch {
            addLocationUseCase.addManuallyTrack(distance * 1000F, start, end)
        }
    }

    fun deleteAndRestore() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.InProgress
            //deleteAndRestoreUseCase()
            _uiState.value = SettingsUiState.Idle
        }
    }

    fun synchronize(onProgress: (Int, Int) -> Unit) {
        viewModelScope.launch {
            try {
                synchronizeUseCase.onProgress = onProgress
                synchronizeUseCase.synchronize()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun dataToSynchronize() =
        synchronizeUseCase.dataToSynchronize()
}
