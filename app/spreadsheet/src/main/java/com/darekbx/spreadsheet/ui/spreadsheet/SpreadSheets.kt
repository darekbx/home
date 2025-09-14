package com.darekbx.spreadsheet.ui.spreadsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.spreadsheet.R
import com.darekbx.spreadsheet.model.SpreadSheet
import com.darekbx.spreadsheet.ui.DeleteConfirmationDialog
import com.darekbx.spreadsheet.ui.ErrorDialog
import com.darekbx.spreadsheet.ui.LoadingBox
import com.darekbx.spreadsheet.ui.theme.BasicSpreadsheetTheme
import com.darekbx.spreadsheet.ui.theme.GREEN
import com.darekbx.spreadsheet.ui.theme.RED
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import de.charlex.compose.rememberRevealState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpreadSheets(
    viewModel: SpreadSheetViewModel = hiltViewModel(),
    openItem: (SpreadSheet) -> Unit = { },
) {
    val uiState by viewModel.uiState.collectAsState()
    var addNewItem by remember { mutableStateOf(false) }
    var deleteConfirmationFor by remember { mutableStateOf<SpreadSheet?>(null) }

    fun deleteItem(item: SpreadSheet) {
        deleteConfirmationFor = item
    }

    LaunchedEffect(Unit) {
        viewModel.fetchSheets()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Spreadsheets") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { addNewItem = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is SpreadSheetUIState.Loading -> LoadingBox()
                is SpreadSheetUIState.Error -> ErrorDialog(state.exception) { viewModel.resetState() }
                is SpreadSheetUIState.Success -> ItemsList(state.items, openItem, ::deleteItem)
                is SpreadSheetUIState.Idle -> {}
            }
        }

        if (addNewItem) {
            NewSpreadSheetDialog(
                parent = true,
                onDismiss = { addNewItem = false },
                onCreate = { parentName, name, columns, rows ->
                    viewModel.createSheet(parentUid = null, parentName, name, columns, rows)
                }
            )
        }

        deleteConfirmationFor?.let {
            DeleteConfirmationDialog(
                message = "Delete ${it.name}?",
                onConfirm = {
                    viewModel.deleteSheet(it)
                    deleteConfirmationFor = null
                },
                onDismiss = { deleteConfirmationFor = null }
            )
        }
    }
}

@Composable
fun ItemsList(
    items: List<SpreadSheet>,
    openItem: (SpreadSheet) -> Unit = { },
    deleteItem: (SpreadSheet) -> Unit = { }
) {
    LazyColumn(
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp)
            .fillMaxWidth()
    ) {
        items(items) { item ->
            val state = rememberRevealState(
                directions = setOf(
                    RevealDirection.EndToStart,
                )
            )
            RevealSwipe(
                modifier = Modifier.padding(8.dp),
                backgroundCardEndColor = RED,
                backgroundCardStartColor = GREEN,
                backgroundStartActionLabel = "",
                backgroundEndActionLabel = "Delete",
                shape = RoundedCornerShape(8.dp),
                card = { shape, shapeContent ->
                    Card(
                        modifier = Modifier.matchParentSize(),
                        colors = CardDefaults.cardColors(
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            containerColor = Color.Transparent
                        ),
                        shape = shape,
                        content = shapeContent
                    )
                },
                onBackgroundEndClick = {
                    deleteItem(item)
                    true
                },
                hiddenContentEnd = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        tint = Color.White,
                        contentDescription = null
                    )
                },
                state = state
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray),
                    shape = it,
                ) {
                    Item(item, openItem)
                }
            }
        }
    }
}

@Composable
private fun Item(
    item: SpreadSheet,
    openItem: (SpreadSheet) -> Unit = { },
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openItem(item) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1F),
            horizontalAlignment = Alignment.Start
        ) {
            val names = item.childrenNames + item.name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier,
                    text = item.parentName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "(${names.joinToString(", ")})",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = buildAnnotatedString {
                    append("Created: ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(item.created)
                    }
                },
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = buildAnnotatedString {
                    append("Updated: ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(item.updated)
                    }
                },
                style = MaterialTheme.typography.labelSmall
            )
        }

        Icon(
            modifier = Modifier
                .padding(end = 8.dp)
                .size(38.dp),
            painter = painterResource(R.drawable.ic_table),
            contentDescription = "Icon",
            tint = Color.DarkGray
        )
    }
}

@Preview
@Composable
private fun SpreadSheetListPreview() {
    BasicSpreadsheetTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ItemsList(
                items = listOf(
                    SpreadSheet("1", "Parent", "Test 1", "2024-06-01", "2024-08-01", 1000).apply {
                        childrenNames = listOf("Child 1", "Child 2", "Child 3")
                    },
                    SpreadSheet("2", "Parent", "Test 2", "2024-06-01", "2025-06-01", 1),
                    SpreadSheet("3", "Parent", "Test 3", "2024-06-01", "2025-06-01", 2),
                    SpreadSheet("4", "Parent", "Test 4", "2024-06-01", "2025-06-01", 3),
                    SpreadSheet("4", "Parent", "Test 5", "2024-06-01", "2025-06-01", 4)
                )
            )
        }
    }
}
