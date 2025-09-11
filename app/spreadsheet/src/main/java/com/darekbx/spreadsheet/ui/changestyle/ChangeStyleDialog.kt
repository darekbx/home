package com.darekbx.spreadsheet.ui.changestyle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import com.darekbx.spreadsheet.model.Style
import com.darekbx.spreadsheet.ui.grid.dialog.AlignRow

@Composable
fun ChangeStyleDialog(
    initialWidth: Int,
    initialAlign: Style.Align,
    onDismiss: () -> Unit,
    onSave: (Int, Style.Align) -> Unit,
) {
    var align by remember { mutableStateOf(initialAlign) }
    var width by remember { mutableStateOf("$initialWidth") }
    var isValid by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change column width") },
        text = {
            Column {
                OutlinedTextField(
                    value = width,
                    onValueChange = { value -> width = value },
                    isError = !isValid,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    label = { Text("Width") }
                )
                AlignRow(align) { align = it }
            }
        },
        confirmButton = {
            Button(onClick = {
                width.toIntOrNull()?.let {
                    onSave(it, align)
                    onDismiss()
                } ?: run {
                    isValid = false
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
