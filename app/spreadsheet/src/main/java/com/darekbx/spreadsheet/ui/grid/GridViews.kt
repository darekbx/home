package com.darekbx.spreadsheet.ui.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darekbx.spreadsheet.ui.grid.bus.SpreadSheetBus
import com.darekbx.spreadsheet.model.Cell
import com.darekbx.spreadsheet.ui.DeleteConfirmationDialog
import com.darekbx.spreadsheet.ui.changestyle.ChangeStyleDialog
import com.darekbx.spreadsheet.ui.theme.cellStyle
import com.darekbx.spreadsheet.ui.theme.READ_ONLY_CELL_COLOR
import com.darekbx.spreadsheet.ui.theme.cellModifier
import com.darekbx.spreadsheet.ui.theme.rowIndexHeight
import kotlinx.coroutines.launch

@Composable
fun TopLeftCorner() {
    Spacer(
        modifier = Modifier
            .width(rowIndexHeight.dp)
            .then(cellModifier)
    )
}

@Composable
fun ColumnHeader(columnIndex: Int, cell: Cell, spreadSheetBus: SpreadSheetBus) {
    val scope = rememberCoroutineScope()
    var showColumnMenu by remember { mutableStateOf(false) }
    var showColumnStyleDialog by remember { mutableStateOf(false) }
    var showDeleteColumnDialog by remember { mutableStateOf(false) }

    CellWrapper(
        modifier = Modifier
            .background(READ_ONLY_CELL_COLOR)
            .clickable { showColumnMenu = true },
        width = cell.width
    ) {
        Text(
            text = Char(columnIndex + 1 + 64).toString(),
            style = cellStyle,
            modifier = Modifier
                .align(Alignment.Center)
        )

        DropdownMenu(
            expanded = showColumnMenu,
            onDismissRequest = { showColumnMenu = false }
        ) {
            MenuItem("Change style") {
                showColumnMenu = false
                showColumnStyleDialog = true
            }
            MenuItem("Add column left") {
                spreadSheetBus.addColumn(columnIndex)
                showColumnMenu = false
            }
            MenuItem("Add column right") {
                spreadSheetBus.addColumn(columnIndex + 1)
                showColumnMenu = false
            }
            MenuItem("Delete column") {
                showDeleteColumnDialog = true
                showColumnMenu = false
            }
        }
    }

    if (showColumnStyleDialog) {
        ChangeStyleDialog(
            initialWidth = cell.width,
            initialAlign = cell.parsedStyle.align,
            onDismiss = { showColumnStyleDialog = false },
            onSave = { newWidth, newAlign ->
                scope.launch {
                    spreadSheetBus.changeColumnStyle(columnIndex, newWidth, newAlign)
                }
            }
        )
    }

    if (showDeleteColumnDialog) {
        DeleteConfirmationDialog(
            message = "Delete column '${Char(columnIndex + 1 + 64)}'?",
            onConfirm = {
                showDeleteColumnDialog = false
                scope.launch { spreadSheetBus.deleteColumn(columnIndex) }
            },
            onDismiss = { showDeleteColumnDialog = false }
        )
    }
}

@Composable
fun RowIndex(rowIndex: Int, spreadSheetBus: SpreadSheetBus) {
    var showRowMenu by remember { mutableStateOf(false) }
    var showDeleteRowDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    CellWrapper(
        modifier = Modifier
            .background(READ_ONLY_CELL_COLOR)
            .clickable { showRowMenu = true },
        width = rowIndexHeight
    ) {
        Text(
            text = "${rowIndex + 1}",
            style = cellStyle,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }

    DropdownMenu(
        expanded = showRowMenu,
        onDismissRequest = { showRowMenu = false }
    ) {
        MenuItem("Add row above") {
            spreadSheetBus.addRow(rowIndex)
            showRowMenu = false
        }
        MenuItem("Add row below") {
            spreadSheetBus.addRow(rowIndex + 1)
            showRowMenu = false
        }
        MenuItem("Delete row") {
            showDeleteRowDialog = true
            showRowMenu = false
        }
    }

    if (showDeleteRowDialog) {
        DeleteConfirmationDialog(
            message = "Delete row ${rowIndex + 1}?",
            onConfirm = {
                showDeleteRowDialog = false
                scope.launch { spreadSheetBus.deleteRow(rowIndex) }
            },
            onDismiss = { showDeleteRowDialog = false }
        )
    }
}

@Composable
private fun MenuItem(label: String, onClick: suspend () -> Unit) {
    val scope = rememberCoroutineScope()
    DropdownMenuItem(
        text = { Text(label) },
        onClick = { scope.launch { onClick() } }
    )
}

@Composable
fun CellWrapper(
    modifier: Modifier = Modifier,
    width: Int,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .width(width.dp)
            .then(cellModifier),
        contentAlignment = Alignment.CenterStart
    ) {
        content()
    }
}
