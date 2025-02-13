package com.darekbx.vault2.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.ConfirmationDialog
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.vault.R
import com.darekbx.vault.data.model.Vault
import com.darekbx.vault.ui.AddDialog
import com.darekbx.vault.ui.VaultViewModel

@Composable
fun ListScreen(vaultViewModel: VaultViewModel = hiltViewModel(), onItemOpen: (Long) -> Unit) {
    val items by vaultViewModel.getItems().collectAsState(initial = emptyList())
    var itemToDelete by remember { mutableStateOf<Vault?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    ListScreen(
        items = items,
        onItemClick = { item -> onItemOpen(item.id!!) },
        onItemAdd = { showAddDialog = true },
        onItemDelete = { item -> itemToDelete = item }
    )

    if (showAddDialog) {
        AddDialog { showAddDialog = false }
    }

    itemToDelete?.let {
        ConfirmationDialog(
            message = "Delete ${it.key}?",
            confirmButtonText = "Delete",
            onDismiss = { itemToDelete = null },
            onConfirm = { vaultViewModel.delete(it.id!!) }
        )
    }
}

@Composable
private fun ListScreen(
    items: List<Vault>,
    onItemClick: (Vault) -> Unit = {},
    onItemAdd: () -> Unit = {},
    onItemDelete: (Vault) -> Unit = {},
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onItemAdd, shape = CircleShape) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        content = { innerPadding ->
            ItemsList(
                modifier = Modifier.padding(innerPadding),
                items = items,
                onItemClick = onItemClick,
                onItemDelete = onItemDelete
            )
        })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ItemsList(
    modifier: Modifier,
    items: List<Vault>,
    onItemClick: (Vault) -> Unit = {},
    onItemDelete: (Vault) -> Unit = {}
) {
    val filter = remember { mutableStateOf("") }
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        stickyHeader {
            SearchField(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                value = filter,
                label = "Search",
                valueError = mutableStateOf(false)
            )
        }
        items(items.filter { it.matchesFilter(filter.value) }) { item ->
            Row(
                modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { onItemClick(item) },
                    onLongClick = { onItemDelete(item) }
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_key),
                    contentDescription = "key"
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = item.key, style = MaterialTheme.typography.titleMedium)
                    Text(text = item.account, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

private fun Vault.matchesFilter(filter: String): Boolean {
    return if (filter.isNotBlank()) {
        key.contains(filter, true) || account.contains(filter, true)
    } else {
        true
    }
}

@Composable
private fun SearchField(
    modifier: Modifier = Modifier,
    label: String,
    value: MutableState<String>,
    valueError: MutableState<Boolean>
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White),
        value = value.value,
        isError = valueError.value,
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

@Preview(device = Devices.PIXEL_6A)
@Composable
fun ItemsListPreview() {
    HomeTheme(isDarkTheme = false) {
        Surface(Modifier.fillMaxSize()) {
            ListScreen((1..10).map { Vault(1, "Key $it", "Account 1", "Password 1") })
        }
    }
}