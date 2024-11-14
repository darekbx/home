package com.darekbx.vault.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.vault.data.model.Vault

@Composable
fun AddDialog(
    vaultViewModel: VaultViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        DialogContents(
            onAdd = { item ->
                vaultViewModel.add(item)
                onDismiss()
            },
            onDismiss = { onDismiss() }
        )
    }
}

@Composable
private fun DialogContents(
    onAdd: (Vault) -> Unit = { },
    onDismiss: () -> Unit = { }
) {
    val key = remember { mutableStateOf("") }
    val account = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val keyError = remember { mutableStateOf(false) }
    val accountError = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add new entry") },
        text = {
            Column {
                InputField("Key", key, keyError)
                InputField("Account", account, accountError)
                InputField("Password", password, passwordError)
            }
        },
        confirmButton = {
            Button(onClick = {

                if (key.value.isBlank()) {
                    keyError.value = true
                    return@Button
                }
                keyError.value = false

                if (account.value.isBlank()) {
                    accountError.value = true
                    return@Button
                }
                accountError.value = false

                if (password.value.isBlank()) {
                    passwordError.value = true
                    return@Button
                }
                passwordError.value = false

                onAdd(Vault(null, key.value, account.value, password.value))

            }) { Text("Add") }
        },
        dismissButton = { Button(onClick = { onDismiss() }) { Text("Cancel") } }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InputField(
    label: String,
    value: MutableState<String>,
    valueError: MutableState<Boolean>
) {
    val focusManager = LocalFocusManager.current
    TextField(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)),
        value = value.value,
        isError = valueError.value,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            errorIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        onValueChange = {
            if (valueError.value) {
                valueError.value = false
            }
            value.value = it
        },
        label = { Text(label) },
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Preview
@Composable
fun DialogPreview() {
    HomeTheme {
        DialogContents()
    }
}
