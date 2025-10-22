package com.darekbx.emailbot.ui.emails

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.emailbot.model.Email
import com.darekbx.emailbot.ui.ErrorView
import com.darekbx.emailbot.ui.ProgressView
import com.darekbx.emailbot.ui.emails.EmailsViewModel.Companion.SPECIAL_EMAILS_FROM
import com.darekbx.emailbot.ui.emails.dialogs.EmailDialog
import com.darekbx.emailbot.ui.emails.dialogs.ReportSpamDialog
import com.darekbx.emailbot.ui.ifTrue
import com.darekbx.emailbot.ui.theme.EmailBotTheme
import com.darekbx.emailbot.ui.theme.Pink40

@Composable
fun EmailsScreen(viewModel: EmailsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var emailToReport by remember { mutableStateOf<Email?>(null) }
    var emailToView by remember { mutableStateOf<Email?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchEmails()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is EmailsUiState.Idle -> { /* NOOP */ }
            is EmailsUiState.Loading -> ProgressView()
            is EmailsUiState.Error -> ErrorView(state.e) { viewModel.resetState() }
            is EmailsUiState.Success -> EmailsList(
                emails = state.emails,
                spamCount = state.spamCount,
                onReportSpam = { emailToReport = it },
                onEmailDelete = { viewModel.deleteEmail(it) },
                onOpenEmail = { emailToView = it }
            )
        }
    }

    emailToReport?.let { spamEmail ->
        ReportSpamDialog(
            email = spamEmail,
            onSave = { from, subject ->
                viewModel.reportSpam(from, subject)
                emailToReport = null
            },
            onDismiss = { emailToReport = null }
        )
    }

    emailToView?.let { email ->
        EmailDialog(
            email = email,
            onDismiss = { emailToView = null }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EmailsList(
    emails: List<Email>,
    spamCount: Int,
    onReportSpam: (Email) -> Unit = {},
    onEmailDelete: (Email) -> Unit = {},
    onOpenEmail: (Email) -> Unit = {}
) {
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        if (spamCount > 0) {
            stickyHeader {
                Text(
                    text = "You have $spamCount spam messages",
                    color = Color(255, 230, 230),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                        .background(Pink40, RoundedCornerShape(8.dp)).padding(4.dp)
                )
            }
        }

        itemsIndexed(items = emails) { index, email ->
            val backgroundColor = if (index % 2 == 0) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
            EmailItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(
                        color = backgroundColor,
                        shape = MaterialTheme.shapes.small
                    ),
                email = email,
                onReportSpam = onReportSpam,
                onEmailDelete = onEmailDelete,
                onOpenEmail = onOpenEmail
            )
        }
    }
}

@Composable
private fun EmailItem(
    modifier: Modifier,
    email: Email,
    onReportSpam: (Email) -> Unit = {},
    onEmailDelete: (Email) -> Unit = {},
    onOpenEmail: (Email) -> Unit = {}
) {
    var reportSpamClicked by remember { mutableStateOf(false) }
    var deleted by remember { mutableStateOf(false) }
    val isSpecial = remember { SPECIAL_EMAILS_FROM.any { email.from.contains(it) } }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .ifTrue(isSpecial) { border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small) }
            .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier
            .weight(1F)
            .alpha(if (deleted) 0.25f else 1f)) {
            if (email.messageId == null) {
                Text(
                    text = "No message ID",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                )
            }
            Text(
                modifier = Modifier,
                text = email.dateTime,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                        )
                    ) { append("From: ") }
                    append(email.from)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                        )
                    ) { append("To: ") }
                    append(email.to)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = email.subject,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { onOpenEmail(email) }) {
                    Text("Open")
                }
                OutlinedButton(onClick = {
                    onEmailDelete(email)
                    deleted = true
                }) {
                    Text("Delete")
                }
                if (!email.isSpam && !reportSpamClicked) {
                    OutlinedButton(onClick = {
                        if (!reportSpamClicked) {
                            onReportSpam(email)
                        }
                        reportSpamClicked = true
                    }) {
                        Text("Report spam")
                    }
                }
            }
        }
        if (email.isSpam || reportSpamClicked) {
            Text(
                text = "Spam",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .rotate(90F)
            )
        }
    }
}

@Preview
@Composable
fun EmailsScreenPreview() {
    EmailBotTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            EmailsList(EmailsViewModel.MOCK_EMAILS, 10)
        }
    }
}
