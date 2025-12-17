package com.darekbx.spreadsheet.ui.grid

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Devices.PIXEL_6A
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.spreadsheet.R
import com.darekbx.spreadsheet.model.Cell
import com.darekbx.spreadsheet.model.SpreadSheet
import com.darekbx.spreadsheet.ui.DeleteConfirmationDialog
import com.darekbx.spreadsheet.ui.ErrorDialog
import com.darekbx.spreadsheet.ui.LoadingBox
import com.darekbx.spreadsheet.ui.changename.ChangeNameDialog
import com.darekbx.spreadsheet.ui.grid.bus.SpreadSheetBus
import com.darekbx.spreadsheet.ui.grid.dialog.ImportDialog
import com.darekbx.spreadsheet.ui.grid.viewmodel.CellsLoader
import com.darekbx.spreadsheet.ui.grid.viewmodel.GridUiState
import com.darekbx.spreadsheet.ui.grid.viewmodel.GridViewModel
import com.darekbx.spreadsheet.ui.spreadsheet.NewSpreadSheetDialog
import com.darekbx.spreadsheet.ui.theme.BORDER_COLOR
import com.darekbx.spreadsheet.ui.theme.BasicSpreadsheetTheme
import com.darekbx.spreadsheet.ui.theme.READ_ONLY_CELL_COLOR
import com.darekbx.spreadsheet.utils.NOOP
import kotlinx.coroutines.launch
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpreadsheetGrid(
    spreadSheetUid: String,
    parentName: String,
    viewModel: GridViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    var addNewItem by remember { mutableStateOf(false) }
    var renameDialog by remember { mutableStateOf(false) }
    var importDialog by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }
    var showSearchBox by remember { mutableStateOf(false) }
    var activeSpreadSheet by remember { mutableStateOf<SpreadSheet?>(null) }

    var selectedCells = remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(spreadSheetUid) {
        viewModel.fetchSpreadSheets(spreadSheetUid)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    activeSpreadSheet?.let {
                        Text("$parentName (${it.name})")
                    }
                },
                actions = {
                    Row {
                        IconButton(onClick = { renameDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Rename")
                        }
                        if (activeSpreadSheet?.uid != spreadSheetUid) {
                            IconButton(onClick = { deleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                        IconButton(onClick = { showSearchBox = !showSearchBox }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is GridUiState.Loading -> LoadingBox()
                is GridUiState.Error -> ErrorDialog(state.exception) { viewModel.resetState() }
                is GridUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (showSearchBox) {
                            SearchBox { phrase ->
                                scope.launch {
                                    selectedCells.value = viewModel.search(phrase)
                                }
                            }
                            if (selectedCells.value.isNotEmpty()) {
                                Text(
                                    "Found: ${selectedCells.value.size} items",
                                    modifier = Modifier.padding(start = 8.dp),
                                    fontSize = 11.sp
                                )
                            }
                            HorizontalDivider(color = Color.Gray)
                        }

                        Grid(
                            spreadSheets = state.spreadSheets,
                            initialSpreadSheet = state.currentSpreadSheet,
                            onAddClick = { addNewItem = true },
                            onImportClick = { importDialog = true },
                            onActiveSheetChange = {
                                viewModel.setCurrentSheetUid(it)
                                activeSpreadSheet = it
                            },
                            spreadSheetBus = viewModel.spreadSheetBus,
                            cellLoader = viewModel.cellLoader,
                            selectedCells = selectedCells
                        )
                    }
                }

                is GridUiState.Idle -> NOOP
            }
        }
    }

    if (addNewItem) {
        NewSpreadSheetDialog(
            parent = false,
            onDismiss = { addNewItem = false },
            onCreate = { _, name, columns, rows ->
                viewModel.createSheet(parentUid = spreadSheetUid, name, columns, rows)
            }
        )
    }

    if (renameDialog) {
        activeSpreadSheet?.let {
            ChangeNameDialog(
                spreadSheetUid = it.uid,
                onDismiss = { renameDialog = false },
                onSave = { newName ->
                    viewModel.fetchSpreadSheets(spreadSheetUid)
                    activeSpreadSheet = activeSpreadSheet?.copy(name = newName)
                }
            )
        }
    }

    if (importDialog) {
        activeSpreadSheet?.let {
            ImportDialog(
                spreadSheetUid = it.uid,
                initialName = activeSpreadSheet?.name ?: "",
                parentName = activeSpreadSheet?.parentName ?: "",
                onDismiss = { importDialog = false },
                onSuccess = { newName ->
                    viewModel.fetchSpreadSheets(spreadSheetUid)
                    activeSpreadSheet = activeSpreadSheet?.copy(name = newName)
                }
            )
        }
    }

    if (deleteDialog) {
        activeSpreadSheet?.let { sheet ->
            DeleteConfirmationDialog(
                message = "Delete \"${sheet.name}\" spreadsheet?",
                onConfirm = {
                    deleteDialog = false
                    viewModel.deleteSheet(sheet)
                },
                onDismiss = { deleteDialog = false }
            )
        }
    }
}

@Composable
private fun SearchBox(onSearch: (String) -> Unit) {
    var searchPhrase by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            value = searchPhrase,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(searchPhrase) }),
            onValueChange = { searchPhrase = it },
            textStyle = TextStyle(fontSize = 14.sp)
        )
        Icon(
            Icons.Default.Search,
            modifier = Modifier.clickable { onSearch(searchPhrase) },
            contentDescription = "Search"
        )
        Spacer(Modifier.width(8.dp))
        Icon(
            Icons.Default.Clear,
            modifier = Modifier.clickable {
                searchPhrase = ""
                onSearch("")
            },
            contentDescription = "Clear"
        )
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
fun ColumnScope.Grid(
    spreadSheets: List<SpreadSheet>,
    initialSpreadSheet: SpreadSheet? = null,
    onAddClick: () -> Unit = { },
    onImportClick: () -> Unit = { },
    onActiveSheetChange: (SpreadSheet) -> Unit = { },
    spreadSheetBus: SpreadSheetBus,
    cellLoader: CellsLoader,
    selectedCells: MutableState<List<String>>
) {
    var isLoading by remember { mutableStateOf(false) }
    var cells by remember { mutableStateOf<List<Cell>>(emptyList()) }
    var activeSpreadSheet by remember { mutableStateOf<SpreadSheet?>(null) }

    suspend fun loadingWrapper(block: suspend () -> Unit) {
        isLoading = true
        block()
        isLoading = false
    }

    LaunchedEffect(activeSpreadSheet, selectedCells.value) {
        activeSpreadSheet?.uid?.let { uid ->
            spreadSheetBus.listenForRefresh()
                .collect {
                    cells = cellLoader.loadCells(uid).map { cell ->
                        cell.copy(isSelected = selectedCells.value.contains(cell.uid))
                    }
                }
        }
    }

    LaunchedEffect(activeSpreadSheet, selectedCells.value) {
        activeSpreadSheet?.uid?.let { uid ->
            loadingWrapper {
                cells = cellLoader.loadCells(uid).map { cell ->
                    cell.copy(isSelected = selectedCells.value.contains(cell.uid))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        activeSpreadSheet = spreadSheets.firstOrNull()
        activeSpreadSheet?.let { spreadSheet ->
            onActiveSheetChange(spreadSheet)
        }
    }

    Grid(modifier = Modifier.weight(1F), cells = cells, spreadSheetBus = spreadSheetBus)
    HorizontalDivider(color = Color.Gray)
    SpreadSheetsRow(
        spreadSheets = spreadSheets,
        initialSpreadSheet = initialSpreadSheet,
        onAddClick = onAddClick,
        onImportClick = onImportClick,
        onActiveSheetChange = {
            activeSpreadSheet = it
            onActiveSheetChange(it)
        }
    )

    if (isLoading) {
        LoadingBox()
    }
}

@Composable
private fun SpreadSheetsRow(
    spreadSheets: List<SpreadSheet>,
    initialSpreadSheet: SpreadSheet? = null,
    onActiveSheetChange: (SpreadSheet) -> Unit = { },
    onAddClick: () -> Unit = { },
    onImportClick: () -> Unit = { }
) {
    var activeSpreadSheet by remember { mutableStateOf(initialSpreadSheet) }

    LaunchedEffect(initialSpreadSheet) {
        activeSpreadSheet = initialSpreadSheet ?: spreadSheets.firstOrNull()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(READ_ONLY_CELL_COLOR)
            .padding(start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(spreadSheets) { spreadSheet ->
                SuggestionChip(
                    onClick = {
                        activeSpreadSheet = spreadSheet
                        onActiveSheetChange(spreadSheet)
                    },
                    label = { Text(text = spreadSheet.name) },
                    border =
                        if (activeSpreadSheet == spreadSheet) BorderStroke(
                            2.dp,
                            Color.Black
                        )
                        else SuggestionChipDefaults.suggestionChipBorder(true),
                    modifier = Modifier.padding(4.dp)
                )
            }
            item {
                Icon(
                    modifier = Modifier
                        .clickable { onAddClick() }
                        .padding(8.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new sheet"
                )
            }
        }
        Icon(
            modifier = Modifier
                .clickable { onImportClick() }
                .padding(8.dp),
            painter = painterResource(R.drawable.ic_import),
            contentDescription = "Import"
        )
    }
}

@Composable
fun Grid(
    modifier: Modifier = Modifier,
    cells: List<Cell> = emptyList(),
    spreadSheetBus: SpreadSheetBus
) {
    val rows = cells.groupBy { it.rowIndex }
    val horizontalScrollState = rememberScrollState()
    val firstRow = rows.values.firstOrNull() ?: emptyList()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(0.25.dp, color = BORDER_COLOR)
    ) {
        Column {
            ColumnHeaders(firstRow, horizontalScrollState, spreadSheetBus)
            DataRows(rows, horizontalScrollState, spreadSheetBus)
        }
    }
}

@Composable
private fun ColumnHeaders(
    cells: List<Cell>,
    horizontalScrollState: ScrollState,
    spreadSheetBus: SpreadSheetBus
) {
    Row(
        modifier = Modifier
            .background(READ_ONLY_CELL_COLOR)
            .horizontalScroll(horizontalScrollState)
    ) {
        TopLeftCorner()
        cells.forEachIndexed { index, cell ->
            ColumnHeader(index, cell, spreadSheetBus)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DataRows(
    rows: Map<Int, List<Cell>>,
    horizontalScrollState: ScrollState,
    spreadSheetBus: SpreadSheetBus
) {
    LazyColumn {
        items(rows.entries.toList()) { (rowIndex, cells) ->
            DataRow(rowIndex, cells, horizontalScrollState, spreadSheetBus)
        }
    }
}

@Composable
private fun DataRow(
    rowIndex: Int,
    cells: List<Cell>,
    horizontalScrollState: ScrollState,
    spreadSheetBus: SpreadSheetBus
) {
    Row(
        modifier = Modifier
            .horizontalScroll(
                state = horizontalScrollState,
                overscrollEffect = null /* Disabled overscroll */
            )
    ) {
        RowIndex(rowIndex = rowIndex, spreadSheetBus = spreadSheetBus)
        cells.forEach { cell ->
            GridCell(initialCell = cell)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = PIXEL_6A)
@Composable
fun GridPreview() {
    BasicSpreadsheetTheme {
        Grid(
            modifier = Modifier
                .padding(top = 48.dp)
                .fillMaxSize(),
            cells = (0..5).flatMap { rowIndex ->
                val widths = listOf(80, 100, 60, 120, 100)
                (0..4).map { colIndex ->
                    Cell(
                        uid = "",
                        sheetUid = "preview",
                        rowIndex = rowIndex,
                        columnIndex = colIndex,
                        value = "R$rowIndex:C$colIndex",
                        width = widths[colIndex]
                    )
                }
            },
            spreadSheetBus = SpreadSheetBus()
        )
    }
}

@Preview
@Composable
fun SpreadSheetsRowPreview() {
    BasicSpreadsheetTheme {
        SpreadSheetsRow(
            spreadSheets = listOf(
                SpreadSheet(
                    uid = "1",
                    parentName = "Parent",
                    name = "Sheet 1",
                    created = "2024-06-01",
                    updated = "2025-06-01",
                    createdTimestamp = 123456789
                ),
                SpreadSheet(
                    uid = "2",
                    parentName = "",
                    name = "Sheet 2",
                    created = "2024-06-01",
                    updated = "2025-06-01",
                    createdTimestamp = 123456789
                ),
                SpreadSheet(
                    uid = "3",
                    parentName = "",
                    name = "Sheet 3",
                    created = "2024-06-01",
                    updated = "2025-06-01",
                    createdTimestamp = 123456789
                ),
                SpreadSheet(
                    uid = "3",
                    parentName = "",
                    name = "Sheet 4",
                    created = "2024-06-01",
                    updated = "2025-06-01",
                    createdTimestamp = 123456789
                ),
            )
        )
    }
}

@Preview
@Composable
fun SearchBoxPreview() {
    BasicSpreadsheetTheme {
        SearchBox { }
    }
}
