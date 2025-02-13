package com.darekbx.vault2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.vault.data.model.Vault
import com.darekbx.vault.ui.VaultViewModel

@Composable
fun ItemScreen(vaultViewModel: VaultViewModel = hiltViewModel(), id: Long) {
    val item by vaultViewModel.getItem(id).collectAsState(initial = null)
    item?.let { secretItem ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Item(secretItem, onSave = { id, key, account, password ->
                vaultViewModel.update(id, key, account, password)
            })

            if (vaultViewModel.inProgress.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(32.dp)
                        .background(Color.Black.copy(0.2F)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}

@Composable
private fun Item(
    secretItem: Vault,
    forceEditMode: Boolean = false,
    onSave: (Long, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    var isEditMode by remember { mutableStateOf(forceEditMode) }

    var key by remember { mutableStateOf(secretItem.key) }
    var account by remember { mutableStateOf(secretItem.account) }
    var password by remember { mutableStateOf(secretItem.password) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isEditMode) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                EditableInputField(key) { newKey -> key = newKey }
                Spacer(modifier = Modifier.height(8.dp))
                EditableInputField(account) { newAccount -> account = newAccount }
                Spacer(modifier = Modifier.height(8.dp))
                EditableInputField(password) { newPassword -> password = newPassword }
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = key,
                    modifier = Modifier.padding(top = 19.dp, bottom = 19.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                InputField(account)
                Spacer(modifier = Modifier.height(8.dp))
                InputField(password)
            }
        }
        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            shape = CircleShape,
            onClick = {
                if (isEditMode) {
                    onSave(secretItem.id!!, key, account, password)
                }
                isEditMode = !isEditMode
            }
        ) {
            if (isEditMode) {
                Icon(Icons.Default.Check, "Save")
            } else {
                Icon(Icons.Default.Edit, "Edit")
            }
        }
    }
}

@Composable
fun EditableInputField(value: String, onChanged: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .background(Color.White),
        value = value,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            errorIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        onValueChange = onChanged,
        shape = RoundedCornerShape(8.dp),
        textStyle = TextStyle(fontSize = 18.sp, textAlign = TextAlign.Center),
        singleLine = true
    )
}

@Composable
fun InputField(value: String) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .background(Color.White),
        value = value,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            errorIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        readOnly = true,
        onValueChange = { },
        shape = RoundedCornerShape(8.dp),
        textStyle = TextStyle(fontSize = 18.sp, textAlign = TextAlign.Center),
        singleLine = true
    )
}

@Preview(device = Devices.PIXEL_6A)
@Composable
fun ItemPreview() {
    HomeTheme(isDarkTheme = false) {
        Surface(Modifier.fillMaxSize()) {
            Item(secretItem = Vault(1, "Key", "Account", "Password"))
        }
    }
}

@Preview(device = Devices.PIXEL_6A)
@Composable
fun EditableItemPreview() {
    HomeTheme(isDarkTheme = false) {
        Surface(Modifier.fillMaxSize()) {
            Item(secretItem = Vault(1, "Key", "Account", "Password"), forceEditMode = true)
        }
    }
}
