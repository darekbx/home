package com.darekbx.infopigula.domain

import com.darekbx.infopigula.repository.Session
import com.darekbx.infopigula.repository.SettingsRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val session: Session
) {

    operator suspend fun invoke() {
        settingsRepository.clearCredentials()
        session.setLoggedOut()
    }
}