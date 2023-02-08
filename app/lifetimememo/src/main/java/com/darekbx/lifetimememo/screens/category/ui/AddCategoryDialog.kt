@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.expenses.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add new category") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
            )
        },
        confirmButton = { Button( onClick = { onAdd(name) }) { Text("Add") } },
        dismissButton = { Button(onClick = { onDismiss() }) { Text("Cancel") } }
    )
}