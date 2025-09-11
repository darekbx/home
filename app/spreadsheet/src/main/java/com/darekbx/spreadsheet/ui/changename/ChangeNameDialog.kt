package com.darekbx.spreadsheet.ui.changename

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.spreadsheet.model.SpreadSheet

@Composable
fun ChangeNameDialog(
    spreadSheetUid: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    viewModel: ChangeNameViewModel = hiltViewModel()
) {
    var spreadSheet by remember { mutableStateOf<SpreadSheet?>(null) }
    var parentName by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var nameIsValid by remember { mutableStateOf(true) }

    LaunchedEffect(spreadSheetUid) {
        viewModel.fetchSheet(spreadSheetUid)?.let {
            spreadSheet = it
            parentName = it.parentName
            name = it.name
        }
    }

    spreadSheet?.let {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Change name") },
            text = {
                Column {
                    if (it.parentUid == null) {
                        OutlinedTextField(
                            value = parentName,
                            onValueChange = { value -> parentName = value },
                            label = { Text("Parent name") }
                        )
                    }
                    OutlinedTextField(
                        value = name,
                        onValueChange = { value -> name = value },
                        isError = !nameIsValid,
                        label = { Text("Name") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isBlank()) {
                        nameIsValid = false
                    } else {
                        viewModel.update(it.copy(name = name, parentName = parentName)) {
                            onSave(name)
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
}
