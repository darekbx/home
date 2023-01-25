package com.darekbx.stocks.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.stocks.R
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel = hiltViewModel()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val state by settingsViewModel.uiState
        when (state) {
            UiState.InProgress -> ProcessingView(Modifier.fillMaxHeight())
            else -> SettingsView { settingsViewModel.importFromArdustocks() }
        }
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_2_XL)
@Composable
private fun SettingsView(onImportClick: () -> Unit = { }) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Settings")
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Please update Ardustocks assets before import!", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        ImportButton { onImportClick() }
    }
}

@Preview
@Composable
fun ProcessingView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Processing...")
    }
}

@Preview
@Composable
private fun ImportButton(onClick: () -> Unit = { }) {
    Button(onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Import from Ardustocks")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onBackground, shape = CircleShape)
                    .padding(4.dp),
                painter = painterResource(id = R.drawable.ic_download),
                contentDescription = "Download"
            )
        }
    }
}
