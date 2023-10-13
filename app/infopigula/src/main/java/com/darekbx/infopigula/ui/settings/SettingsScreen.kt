package com.darekbx.infopigula.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.infopigula.ui.ProgressIndicator

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel = hiltViewModel()) {
    val isDarkMode by settingsViewModel.isDarkMode
    val uiState by settingsViewModel.uiState.collectAsState(initial = SettingsUiState.Idle)
    val groups = settingsViewModel.groups

    var groupsString by remember { mutableStateOf("") }

    LaunchedEffect(groups) {
        groupsString = groups.joinToString(",")
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Settings",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Dark mode",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Switch(
                checked = isDarkMode,
                onCheckedChange = { settingsViewModel.setDarkTheme(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onBackground,
                    checkedTrackColor = MaterialTheme.colorScheme.background,
                    checkedBorderColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Excluded groups",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = groupsString,
                onValueChange = {
                    groupsString = it
                }
            )
            Button(onClick = { settingsViewModel.save(groupsString) }) {
                Text(text = "Save", color = MaterialTheme.colorScheme.background)
            }
        }
    }

    if (uiState is SettingsUiState.InProgress) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(5.dp),
            contentAlignment = Alignment.Center
        ) {
            ProgressIndicator()
        }
    }
}