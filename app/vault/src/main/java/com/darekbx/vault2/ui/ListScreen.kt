package com.darekbx.vault2.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.vault.ui.VaultViewModel

@Composable
fun ListScreen(vaultViewModel: VaultViewModel = hiltViewModel(), onItemClick: (Long) -> Unit) {
    Text("New List screen")
}