@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.vault.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.ConfirmationDialog
import com.darekbx.vault.R
import com.darekbx.vault.data.model.Vault

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListScreen(vaultViewModel: VaultViewModel = hiltViewModel(), onItemClick: (Long) -> Unit) {
    val items by vaultViewModel.getItems().collectAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var clickedItem by remember { mutableStateOf(null as Vault?) }

    LaunchedEffect(Unit) {
        vaultViewModel.prepareLegacyData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Password Vault",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, shape = CircleShape) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(items) { item ->
                        Row(modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { onItemClick(item.id!!) },
                                onLongClick = {
                                    clickedItem = item
                                    showDeleteDialog = true
                                }
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_key),
                                contentDescription = "key"
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = item.key, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                if (showAddDialog) {
                    AddDialog {
                        showAddDialog = false
                    }
                }

                if (showDeleteDialog && clickedItem != null) {
                    ConfirmationDialog(
                        message = "Delete ${clickedItem?.key}?",
                        confirmButtonText = "Delete",
                        onDismiss = {
                            clickedItem = null
                            showDeleteDialog = false
                        },
                        onConfirm = { vaultViewModel.delete(clickedItem?.id!!) }
                    )
                }
            }
        })
}