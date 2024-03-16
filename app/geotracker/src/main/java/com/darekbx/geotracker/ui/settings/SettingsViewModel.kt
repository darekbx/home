package com.darekbx.geotracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.geotracker.BuildConfig
import com.darekbx.geotracker.domain.usecase.DeleteAndRestoreUseCase
import com.darekbx.geotracker.domain.usecase.SynchronizeUseCase
import com.darekbx.geotracker.repository.SettingsRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SettingsUiState {
    object Idle : SettingsUiState()
    object InProgress : SettingsUiState()
    data class Done(
        val nthPointsToSkip: Int,
        val gpsMinDistance: Float,
        val gpsUpdateInterval: Long,
        val showYearSummaryValue: Boolean
    ) :
        SettingsUiState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val deleteAndRestoreUseCase: DeleteAndRestoreUseCase,
    private val settingsRepository: SettingsRepository,
    private val synchronizeUseCase: SynchronizeUseCase
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

            _uiState.value =
                SettingsUiState.Done(
                    nthPointsToSkip,
                    gpsMinDistance,
                    gpsUpdateInterval,
                    showYearSummaryValue
                )
        }
    }

    fun save(
        nthPointsToSkip: Int,
        gpsMinDistance: Float,
        gpsUpdateInterval: Long,
        showYearSummaryValue: Boolean
    ) {
        viewModelScope.launch {
            settingsRepository.saveSettings(
                nthPointsToSkip,
                gpsMinDistance,
                gpsUpdateInterval,
                showYearSummaryValue
            )

            refresh()
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
                synchronizeUseCase.synchronize(
                    BuildConfig.CLOUD_EMAIL,
                    BuildConfig.CLOUD_PASSWORD
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun dataToSynchronize() =
        synchronizeUseCase.dataToSynchronize(
            BuildConfig.CLOUD_EMAIL,
            BuildConfig.CLOUD_PASSWORD
        )
}
