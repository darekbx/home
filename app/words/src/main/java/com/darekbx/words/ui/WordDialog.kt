package com.darekbx.words.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun WordDialog(
    onDismiss: () -> Unit = { },
    onWordAdded: (String, String) -> Unit = { _, _ -> },
    maxRating: Int = 5
) {
    var word by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add new word") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = word,
                    onValueChange = { word = it },
                    label = { Text(text = "Word") })
                TextField(
                    value = translation,
                    onValueChange = { translation = it },
                    label = { Text(text = "Translation") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onWordAdded(word, translation)
                onDismiss()
            }) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        }
    )
}