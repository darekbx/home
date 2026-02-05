package com.darekbx.spreadsheet.ui.spreadsheet

import android.text.format.Formatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.darekbx.spreadsheet.domain.SyncStatus
import com.darekbx.spreadsheet.ui.theme.BasicSpreadsheetTheme
import com.darekbx.spreadsheet.ui.theme.GREEN_DARK

@Composable
fun SyncDialog(viewModel: SpreadSheetViewModel, onDismiss: () -> Unit) {
    val syncState by viewModel.syncState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.synchronize()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        SyncCard(syncState, onDismiss)
    }
}

@Composable
private fun SyncCard(state: SyncStatus, onDismiss: () -> Unit = {}) {
    Card {
        Box(
            modifier = Modifier
                .size(256.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Synchronization",
                modifier = Modifier.align(Alignment.TopCenter),
                style = MaterialTheme.typography.titleSmall
            )
            Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                when (state) {
                    is SyncStatus.Error -> ErrorMessage(state.e, onDismiss)
                    is SyncStatus.InProgress -> SyncProgress(state.progress)
                    is SyncStatus.Success -> SyncSucces(state.maxBlobSize, onDismiss)
                    is SyncStatus.UpToDate -> VersionMessage(state, onDismiss)
                    SyncStatus.Idle -> NoOp()
                }
            }
        }
    }
}

@Composable
private fun SyncSucces(maxBlobSize: Long, onBack: () -> Unit) {
    val context = LocalContext.current
    val maxSizeFormatted = remember { Formatter.formatShortFileSize(context, maxBlobSize) }
    val oneMBFormatted = remember { Formatter.formatShortFileSize(context, 1024L * 1024L) }
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = GREEN_DARK)) {
                append("Sync completed!\n")
            }
            withStyle(SpanStyle(fontSize = 11.sp)) {
                append("max blob size: $maxSizeFormatted of $oneMBFormatted")
            }
        },
        modifier = Modifier.clickable { onBack() },
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        lineHeight = 24.sp,
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
private fun VersionMessage(upToDate: SyncStatus.UpToDate, onBack: () -> Unit) {
    val (localVersion: Long, remoteVersion:Long) = upToDate
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = GREEN_DARK)) {
                append("You are up to date!\n")
            }
            withStyle(SpanStyle(fontSize = 11.sp)) {
                append("local: v$localVersion, remote: v$remoteVersion")
            }
        },
        modifier = Modifier.clickable { onBack() },
        textAlign = TextAlign.Center,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
private fun ErrorMessage(e: Throwable, onBack: () -> Unit) {
    Text(
        text = e.toString(),
        modifier = Modifier.clickable { onBack() },
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.error,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
private fun SyncProgress(progress: Float) {
    CircularProgressIndicator(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        strokeWidth = 8.dp,
        progress = { progress })
    Text(
        text = "${(progress * 100F).toInt()}%",
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
private fun NoOp() = Unit

@Preview
@Composable
private fun SyncStatusSuccessPreview() {
    BasicSpreadsheetTheme {
        SyncCard(SyncStatus.Success(41221))
    }
}

@Preview
@Composable
private fun SyncStatusProgressPreview() {
    BasicSpreadsheetTheme {
        SyncCard(SyncStatus.InProgress(0.4F))
    }
}

@Preview
@Composable
private fun SyncStatusErrorPreview() {
    BasicSpreadsheetTheme {
        SyncCard(SyncStatus.Error(IllegalStateException("Some serious issue!")))
    }
}

@Preview
@Composable
private fun SyncStatusVersionsErrorPreview() {
    BasicSpreadsheetTheme {
        SyncCard(SyncStatus.UpToDate(2, -1))
    }
}

