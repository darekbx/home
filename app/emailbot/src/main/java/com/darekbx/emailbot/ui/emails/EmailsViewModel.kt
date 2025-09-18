package com.darekbx.emailbot.ui.emails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.emailbot.BuildConfig
import com.darekbx.emailbot.bot.markSpam
import com.darekbx.emailbot.domain.AddSpamFilterUseCase
import com.darekbx.emailbot.domain.FetchSpamFiltersUseCase
import com.darekbx.emailbot.imap.EmailOperations
import com.darekbx.emailbot.imap.FetchEmails
import com.darekbx.emailbot.model.Email
import com.darekbx.emailbot.model.EmailContent
import com.darekbx.emailbot.repository.RefreshBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface EmailsUiState {
    data object Idle : EmailsUiState
    data object Loading : EmailsUiState
    data class Error(val e: Throwable) : EmailsUiState
    data class Success(val emails: List<Email>) : EmailsUiState
}

@HiltViewModel
class EmailsViewModel @Inject constructor(
    private val fetchEmails: FetchEmails,
    private val addSpamFilterUseCase: AddSpamFilterUseCase,
    private val fetchSpamFiltersUseCase: FetchSpamFiltersUseCase,
    private val emailOperations: EmailOperations,
    private val refreshBus: RefreshBus
) : ViewModel() {

    private val _uiState = MutableStateFlow<EmailsUiState>(EmailsUiState.Idle)
    val uiState: StateFlow<EmailsUiState> = _uiState.asStateFlow()

    init {
        listenForChanges()
    }

    private fun listenForChanges() {
        viewModelScope.launch {
            refreshBus.listenForChanges().collect {
                fetchEmails()
            }
        }
    }

    fun fetchEmails() {
        viewModelScope.launch {
            _uiState.value = EmailsUiState.Loading
            try {
                val spamFilters = fetchSpamFiltersUseCase.invoke()
                //val emails = MOCK_EMAILS
                val emails = fetchEmails.fetch()
                    .sortedByDescending { it.messageNumber }
                emails.markSpam(spamFilters)
                val emailsWithoutSpam = emails.filter { !it.isSpam }
                _uiState.value = EmailsUiState.Success(emailsWithoutSpam)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                _uiState.value = EmailsUiState.Error(e)
            }
        }
    }

    fun reportSpam(from: String, subject: String) {
        viewModelScope.launch {
            addSpamFilterUseCase.invoke(
                from.takeIf { it.isNotBlank() },
                subject.takeIf { it.isNotBlank() }
            )
        }
    }

    fun deleteEmail(email: Email) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    emailOperations.removeEmail(email.messageNumber)
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                _uiState.value = EmailsUiState.Error(e)
            }
        }
    }

    fun resetState() {
        _uiState.value = EmailsUiState.Idle
    }

    companion object {
        val SPECIAL_EMAILS_FROM = listOf(
            "mrugalski.pl",
            "infinum.email",
            "sebastianchudziak.pl"
        )
        val MOCK_EMAILS = listOf(
            Email(
                "17x76357.1833303.1670218686@info.fastymail.pl",
                0,
                "sts@digitaldynamicsx.pl",
                "anyone@o2-all.pl",
                "Trzaskowski czy Nawrocki?\uD83E\uDD14 Kto zostanie prezydentem? Sprawdź, ile możesz wygrać!\uD83E\uDD11",
                EmailContent.Unknown,
                "2025-05-29 08:16:11"
            ).also { it.isSpam = true },
            Email(
                "17x76331.1833453.1670628738@info.fastymail.pl",
                1,
                "depilacja@ecommercefunnel.pl",
                "anyone@o2-all.pl",
                "Efekty już po 2 zabiegach! Zadbaj o siebie z depilatorem Braun>>",
                EmailContent.Unknown,
                "2025-05-29 09:00:28"
            ),
            Email(
                null,
                2,
                "accounts.mailerlite@infinum.email",
                "anyone@o2-all.pl",
                "Process Death Is Inevitable",
                EmailContent.Unknown,
                "2025-05-29 09:01:31"
            )
        )
    }
}
