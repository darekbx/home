package com.darekbx.lifetimememo.screens.memos.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmationDialog(
    message: AnnotatedString,
    confirmButtonText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(modifier = Modifier.padding(vertical = 8.dp), text = "Please confirm") },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) { Text(confirmButtonText) }
        },
        dismissButton = { Button(onClick = { onDismiss() }) { Text("Cancel") } }
    )
}