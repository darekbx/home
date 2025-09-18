package com.darekbx.notebookcheckreader

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.LauncherActivity
import com.darekbx.notebookcheckreader.navigation.AppNavHost
import com.darekbx.notebookcheckreader.navigation.FavouritesDestination
import com.darekbx.notebookcheckreader.ui.MainUiState
import com.darekbx.notebookcheckreader.ui.MainViewModel
import com.darekbx.notebookcheckreader.ui.theme.NotebookcheckReaderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotebookcheckActivity : LauncherActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotebookcheckReaderTheme {
                val navController = rememberNavController()
                val mainViewModel = hiltViewModel<MainViewModel>()
                val state by mainViewModel.uiState.collectAsStateWithLifecycle()
                val favouritesCount by mainViewModel.favouritesCount()
                    .collectAsStateWithLifecycle(initialValue = 0)
                val itemsCount by mainViewModel.itemsCount()
                    .collectAsStateWithLifecycle(initialValue = 0)

                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    AppBar(
                        itemsCount = itemsCount,
                        favouritesCount = favouritesCount,
                        onSyncClick = { mainViewModel.synchronize() },
                        onFavouritesClick = { navController.navigate(FavouritesDestination.route) },
                        onDeleteClick = { mainViewModel.deleteOldItems() }
                    )
                }) { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppNavHost(navController, modifier = Modifier)

                        if (state is MainUiState.Loading) {
                            LoadingBox()
                        }
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }
    }

    @Composable
    private fun LoadingBox() {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75F)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(Modifier.size(64.dp), color = Color.White)
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun AppBar(
        itemsCount: Int,
        favouritesCount: Int,
        onSyncClick: () -> Unit = {},
        onFavouritesClick: () -> Unit = {},
        onDeleteClick: () -> Unit = {}
    ) {
        Surface(shadowElevation = 4.dp) {
            TopAppBar(
                title = { Text("Notebookcheck ($itemsCount)") },
                colors = TopAppBarDefaults.topAppBarColors(),
                actions = {
                    Row {
                        Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            IconButton(onClick = onDeleteClick) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete"
                                )
                            }
                        }
                        Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            IconButton(onClick = onFavouritesClick) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = "Favourites"
                                )
                            }
                            if (favouritesCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.error,
                                            CircleShape
                                        )
                                        .size(18.dp)
                                        .aspectRatio(1F)
                                        .align(Alignment.BottomStart),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "$favouritesCount",
                                        textAlign = TextAlign.Center,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.absoluteOffset(y = -3.dp)
                                    )
                                }
                            }
                        }
                        Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            IconButton(onClick = onSyncClick) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh"
                                )
                            }
                        }
                    }
                })
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
            }

            shouldShowRequestPermissionRationale(permission) -> {
                showPermissionRationaleDialog()
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notification Permission")
            .setMessage("This app needs notification permission to show work results.")
            .setPositiveButton("OK") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
