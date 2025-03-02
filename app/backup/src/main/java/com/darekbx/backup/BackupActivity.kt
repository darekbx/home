package com.darekbx.backup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.LauncherActivity
import com.darekbx.common.ui.theme.HomeTheme
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class BackupActivity : LauncherActivity() {

    private val backupViewModel: BackupViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        /* NOOP */
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HomeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var restoreDialogShown by remember { mutableStateOf(false) }

                    Column(modifier = Modifier.padding(16.dp)) {
                        LastBackupDate()
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Button(onClick = { onBackupRequest() }) {
                                Text(text = "Make backup")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(onClick = { restoreDialogShown = true }) {
                                Text(text = "Restore")
                            }
                        }
                    }

                    if (restoreDialogShown) {
                        RestoreConfirmationDialog(
                            onDismiss = { restoreDialogShown = false },
                            onRestore = { restore() })
                    }
                }
            }
        }

        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.data = Uri.parse("package:" + this.packageName)
        requestPermissionLauncher.launch(intent)
    }

    private fun restore() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
        }
        restoreRequest.launch(intent)
    }

    @Composable
    private fun LastBackupDate(backupViewModel: BackupViewModel = hiltViewModel()) {
        val data by backupViewModel.lastBackupDate.collectAsState(initial = "")
        Text(text = "Last backup date: $data")
    }

    @Composable
    fun RestoreConfirmationDialog(
        onDismiss: () -> Unit,
        onRestore: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(modifier = Modifier.padding(vertical = 8.dp), text = "Please confirm") },
            text = { Text(text = "Please select backup file to restore.\n\nBefore restore clear app data and grant all permissions! \n\nWARNING: restore will override current database!") },
            confirmButton = {
                Button(onClick = {
                    onRestore()
                    onDismiss()
                }) { Text("Restore") }
            },
            dismissButton = { Button(onClick = { onDismiss() }) { Text("Cancel") } }
        )
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

    private val restoreRequest = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultIntent ->
        resultIntent.data?.data?.let { resultUri ->
            contentResolver.openFileDescriptor(resultUri, "r")?.use { pfd ->
                backupViewModel.restoreBackup(pfd) {
                    Toast.makeText(applicationContext, "Data was restored", Toast.LENGTH_SHORT).show()
                }
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

