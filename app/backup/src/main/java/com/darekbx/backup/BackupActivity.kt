package com.darekbx.backup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.theme.HomeTheme
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class BackupActivity : ComponentActivity() {

    private val backupViewModel: BackupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HomeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        LastBackupDate()
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Button(onClick = { onBackupRequest() }) {
                                Text(text = "Make backup")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(modifier = Modifier.alpha(0.5F), onClick = { }) {
                                Text(text = "Restore")
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun LastBackupDate(backupViewModel: BackupViewModel = hiltViewModel()) {
        val data by backupViewModel.lastBackupDate.collectAsState(initial = "")
        Text(text = "Last backup date: $data")
    }

    private val backupResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultIntent ->
        resultIntent.data?.data?.let { resultUri ->
            contentResolver.openFileDescriptor(resultUri, "w")?.use { pfd ->
                backupViewModel.makeBackup(pfd)
            }
        }
    }

    private fun onBackupRequest() {
        val fileDataFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val dateTime = fileDataFormat.format(Date())
        val mimeTypes = arrayOf("application/x-sqlite3")
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("application/x-sqlite3")
            .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            .putExtra(Intent.EXTRA_TITLE, "home_backup_$dateTime.sqlite")
        backupResult.launch(intent)
    }
}
