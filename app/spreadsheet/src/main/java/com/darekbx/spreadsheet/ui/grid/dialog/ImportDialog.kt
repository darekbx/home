package com.darekbx.spreadsheet.ui.grid.dialog

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.spreadsheet.ui.ErrorDialog
import com.darekbx.spreadsheet.ui.LoadingBoxRounded
import com.darekbx.spreadsheet.ui.grid.viewmodel.ImportUiState
import com.darekbx.spreadsheet.ui.grid.viewmodel.ImportViewModel
import com.darekbx.spreadsheet.utils.NOOP

@Composable
fun ImportDialog(
    spreadSheetUid: String,
    initialName: String = "",
    parentName: String = "",
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit,
    viewModel: ImportViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf(initialName) }
    var nameIsValid by remember { mutableStateOf(true) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedFileUri = uri }

    val uiState by viewModel.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Cell") },
        text = {
            Box(Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameIsValid = it.isNotBlank()
                        },
                        isError = !nameIsValid,
                        label = { Text("Name") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { filePickerLauncher.launch("text/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (selectedFileUri != null) "Change File" else "Select CSV File")
                    }

                    if (selectedFileUri != null) {
                        Text(
                            text = "Selected: $selectedFileUri",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Box(Modifier.matchParentSize()) {
                    when (val state = uiState) {
                        ImportUiState.Success -> {
                            onDismiss()
                            onSuccess(name)
                        }
                        is ImportUiState.Error -> {
                            ErrorDialog(exception = state.exception) {
                                viewModel.resetState()
                            }
                        }
                        ImportUiState.Loading -> LoadingBoxRounded()
                        ImportUiState.Idle -> NOOP
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (nameIsValid) {
                    viewModel.importSheet(spreadSheetUid, name, parentName, selectedFileUri)
                }
            }) {
                Text("Import")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
