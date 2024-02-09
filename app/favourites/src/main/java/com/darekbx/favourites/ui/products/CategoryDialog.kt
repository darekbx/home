package com.darekbx.favourites.ui.products

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

@Preview
@Composable
fun CategoryDialog(
    onDismiss: () -> Unit = { },
    onCategoryAdded: (String) -> Unit = { }
) {
    var categoryName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add new category") },
        text = { TextField(value = categoryName, onValueChange = { categoryName = it }) },
        confirmButton = {
            TextButton(onClick = {
                onCategoryAdded(categoryName)
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
