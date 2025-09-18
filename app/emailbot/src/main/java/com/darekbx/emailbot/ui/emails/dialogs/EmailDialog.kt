package com.darekbx.emailbot.ui.emails.dialogs

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices.PIXEL_6A
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.darekbx.emailbot.model.Email
import com.darekbx.emailbot.model.EmailContent
import com.darekbx.emailbot.ui.emails.EmailsViewModel
import com.darekbx.emailbot.ui.theme.EmailBotTheme

@Composable
fun EmailDialog(email: Email, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
                .padding(vertical = 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                )

        ) {
            Column(Modifier.padding(8.dp)) {
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
            }

            when (val content = email.content) {
                is EmailContent.Html -> HtmlContent(content)
                is EmailContent.Mixed -> MixedContent(content)
                is EmailContent.Text -> TextContent(content)
                EmailContent.Unknown -> {}
            }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.End)
            ) {
                Text("Close")
            }
        }
    }
}

@Composable
private fun TextContent(content: EmailContent.Text) {
    Text(
        modifier = Modifier.padding(8.dp),
        text = content.text,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun ColumnScope.HtmlContent(content: EmailContent.Html) {
    Box(
        Modifier
            .fillMaxWidth()
            .weight(1F)
            .padding(4.dp)
        .clip(RoundedCornerShape(8.dp))
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = false
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL(
                    null,
                    content.html,
                    "text/html",
                    "UTF-8",
                    null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
private fun ColumnScope.MixedContent(content: EmailContent.Mixed) {
    HtmlContent(EmailContent.Html(content.htmlContent ?: content.textContent))
}

@Preview(showBackground = true, device = PIXEL_6A)
@Composable
private fun EmailDialogUnknownPreview() {
    val mockEmail = EmailsViewModel.Companion.MOCK_EMAILS[0]
    EmailBotTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            EmailDialog(
                email = mockEmail,
                onDismiss = { }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmailDialogTextPreview() {
    val mockEmail = EmailsViewModel.Companion.MOCK_EMAILS[0].copy(
        content = EmailContent.Text("This is a text content of the email.")
    )
    EmailBotTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            EmailDialog(
                email = mockEmail,
                onDismiss = { }
            )
        }
    }
}
