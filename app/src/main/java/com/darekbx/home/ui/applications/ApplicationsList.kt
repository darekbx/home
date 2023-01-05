package com.darekbx.home.ui.applications

import android.content.ComponentName
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ApplicationsList(applicationsViewModel: ApplicationsViewModel = hiltViewModel()) {
    Column() {
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Button")
        }
        Text(text = "Apps: ${applicationsViewModel.applications().joinToString(", ") { it.label }}")
    }
}