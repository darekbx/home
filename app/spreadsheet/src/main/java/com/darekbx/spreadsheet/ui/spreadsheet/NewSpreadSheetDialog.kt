package com.darekbx.spreadsheet.ui.spreadsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.spreadsheet.ui.theme.BasicSpreadsheetTheme

@Composable
fun NewSpreadSheetDialog(
    parent: Boolean,
    onDismiss: () -> Unit,
    onCreate: (parentName: String, name: String, columns: Int, rows: Int) -> Unit
) {
    var parentName by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val columns = remember { mutableStateOf("1") }
    val rows = remember { mutableStateOf("1") }

    var nameIsValid by remember { mutableStateOf(true) }
    val columnsIsValid = remember { mutableStateOf(true) }
    val rowsIsValid = remember { mutableStateOf(true) }

    val nameFocusRequester = remember { FocusRequester() }
    val columnsFocusRequester = remember { FocusRequester() }
    val rowsFocusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New spreadsheet") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (parent) {
                    OutlinedTextField(
                        value = parentName,
                        onValueChange = { parentName = it },
                        label = { Text("Parent name") },
                        keyboardActions = KeyboardActions(
                            onNext = { nameFocusRequester.requestFocus() }
                        )
                    )
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameIsValid = it.isNotBlank()
                    },
                    isError = !nameIsValid,
                    label = { Text("Name") },
                    keyboardActions = KeyboardActions(
                        onNext = { columnsFocusRequester.requestFocus() }
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ValidatedTextField("Columns", columns, columnsIsValid, rowsFocusRequester)
                    Text("x", modifier = Modifier.padding(start = 8.dp, end = 8.dp))
                    ValidatedTextField("Rows", rows, rowsIsValid)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                when {
                    name.isBlank() -> nameIsValid = false
                    !columns.validateInt() -> columnsIsValid.value = false
                    !rows.validateInt() -> rowsIsValid.value = false
                    else -> {
                        onCreate(parentName, name, columns.value.toInt(), rows.value.toInt())
                        onDismiss()
                    }
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

@Composable
private fun RowScope.ValidatedTextField(
    label: String,
    columns: MutableState<String>,
    columnsIsValid: MutableState<Boolean>,
    focusRequester: FocusRequester? = null
) {
    OutlinedTextField(
        modifier = Modifier.weight(1F),
        value = columns.value,
        isError = !columnsIsValid.value,
        onValueChange = { value ->
            val filteredValue = value.filter { it.isDigit() }
            columns.value = filteredValue
            columnsIsValid.value = filteredValue.validateInt()
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusRequester?.requestFocus() }
        ),
        label = { Text(label) }
    )
}

private fun MutableState<String>.validateInt(): Boolean {
    return value.validateInt()
}

private fun String.validateInt(): Boolean {
    return toIntOrNull()?.let { it > 0 } ?: false
}

@Preview
@Composable
fun NewSpreadSheetDialogPreview() {
    BasicSpreadsheetTheme {
        NewSpreadSheetDialog(
            parent = true,
            onDismiss = { },
            onCreate = { _, _, _, _ -> }
        )
    }
}
