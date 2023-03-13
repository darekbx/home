@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.darekbx.books.ui.toread

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.books.data.model.ToRead
import com.darekbx.books.ui.ConfirmationDialog
import com.darekbx.books.ui.LocalColors

@Composable
fun ToReadScreen(toReadViewModel: ToReadViewModel = hiltViewModel()) {
    val items by toReadViewModel.items().collectAsState(initial = emptyList())
    var deleteToReadDialog by remember { mutableStateOf(false) }
    var showToReadDialog by remember { mutableStateOf(false) }
    var clickedToRead by remember { mutableStateOf(null as ToRead?) }

    /*
    //Uncomment to fill data from legacy db
    LaunchedEffect(Unit) {
        toReadViewModel.fillData()
    }
     */

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "To Read",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    titleContentColor = Color.White,
                    containerColor = LocalColors.current.green
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showToReadDialog = true }, shape = CircleShape) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ToReadList(
                    items,
                    onToReadLongClick = {
                        clickedToRead = it
                        deleteToReadDialog = true
                    }
                )

                if (showToReadDialog) {
                    ToReadDialog {
                        showToReadDialog = false
                    }
                }

                if (deleteToReadDialog) {
                    ConfirmationDialog(
                        message = "Delete ${clickedToRead?.author} ${clickedToRead?.title}?",
                        confirmButtonText = "Delete",
                        onDismiss = {
                            clickedToRead = null
                            deleteToReadDialog = false
                        },
                        onConfirm = { toReadViewModel.delete(clickedToRead) }
                    )
                }
            }
        }
    )
}


@Composable
private fun ToReadList(
    items: List<ToRead>,
    onToReadLongClick: (ToRead) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        items(items, key = {item -> item.id!! }) { item ->
            ItemEntryView(
                item,
                onLongClick = { onToReadLongClick(item) }
            )
        }
    }
}

@Composable
private fun ItemEntryView(toRead: ToRead, onLongClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 8.dp, top = 4.dp)
            .combinedClickable(
                onClick = { /* Do nothing */ },
                onLongClick = onLongClick
            ),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = toRead.author,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = toRead.title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
