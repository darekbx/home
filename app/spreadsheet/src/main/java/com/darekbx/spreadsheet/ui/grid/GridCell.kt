package com.darekbx.spreadsheet.ui.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.spreadsheet.R
import com.darekbx.spreadsheet.model.Cell
import com.darekbx.spreadsheet.model.Style
import com.darekbx.spreadsheet.model.Style.Companion.toJson
import com.darekbx.spreadsheet.ui.grid.dialog.CellDialog
import com.darekbx.spreadsheet.ui.grid.viewmodel.CellViewModel
import com.darekbx.spreadsheet.ui.theme.BasicSpreadsheetTheme
import com.darekbx.spreadsheet.ui.theme.RED
import com.darekbx.spreadsheet.ui.theme.cellStyle

@Composable
fun GridCell(initialCell: Cell, viewModel: CellViewModel = hiltViewModel()) {
    var currentCell: Cell by remember(initialCell) { mutableStateOf(initialCell) }
    var functionResult by remember { mutableStateOf<Result<String>?>(null) }
    var isDialogVisible: Boolean by remember { mutableStateOf(false) }

    val referencedRows = remember(currentCell.formula) {
        viewModel.getCellReferences(currentCell)
    }

    LaunchedEffect(currentCell.formula) {
        functionResult = viewModel.invokeFunction(currentCell)
    }

    LaunchedEffect(referencedRows) {
        if (referencedRows.isNotEmpty()) {
            // Listen for changes in referenced cells and recalculate function result if needed
            viewModel.cellChanges.collect { changedCell ->
                val isReferencedCell =
                    changedCell.sheetUid == currentCell.sheetUid &&
                            changedCell.columnIndex == currentCell.columnIndex &&
                            (changedCell.rowIndex + 1) in referencedRows

                if (isReferencedCell) {
                    functionResult = viewModel.invokeFunction(currentCell)
                }
            }
        }
    }

    functionResult
        ?.let { result ->
            FunctionContent(currentCell, result) {
                isDialogVisible = true
            }
        } ?: run {
        CellContent(currentCell) {
            isDialogVisible = true
        }
    }

    if (isDialogVisible) {
        CellDialog(
            cell = currentCell,
            onDismiss = { isDialogVisible = false },
            onSave = { updatedCell ->
                viewModel.updateCell(updatedCell)
                currentCell = updatedCell
            }
        )
    }
}

@Composable
private fun CellContent(
    cell: Cell,
    onCellClick: () -> Unit
) {
    val style = cell.parsedStyle
    val selectedModifier = if (cell.isSelected) Modifier.border(2.dp, RED) else Modifier
    CellWrapper(
        modifier = Modifier
            .clickable { onCellClick() }
            .background(style.composeColor())
            .then(selectedModifier),
        width = cell.width
    ) {
        val alignment = when (style.align) {
            Style.Align.LEFT -> Alignment.CenterStart
            Style.Align.CENTER -> Alignment.Center
            Style.Align.RIGHT -> Alignment.CenterEnd
        }
        Text(
            modifier = Modifier.align(alignment),
            text = cell.value,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontWeight = if (style.bold) FontWeight.Bold else FontWeight.Normal,
            style = cellStyle
        )
    }
}

@Composable
private fun FunctionContent(
    cell: Cell,
    result: Result<String>,
    onCellClick: () -> Unit = { }
) {
    val style = cell.parsedStyle
    CellWrapper(
        modifier = Modifier
            .clickable { onCellClick() }
            .background(style.composeColor()),
        width = cell.width
    ) {
        val alignment = when (style.align) {
            Style.Align.LEFT -> Alignment.CenterStart
            Style.Align.CENTER -> Alignment.Center
            Style.Align.RIGHT -> Alignment.CenterEnd
        }

        if (result.isFailure) {
            Text(
                modifier = Modifier.align(alignment),
                text = "#ERROR",
                color = Color.Red,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontWeight = if (style.bold) FontWeight.Bold else FontWeight.Normal,
                style = cellStyle
            )
        } else {
            Text(
                modifier = Modifier.align(alignment),
                text = result.getOrNull() ?: cell.value,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontWeight = if (style.bold) FontWeight.Bold else FontWeight.Normal,
                style = cellStyle
            )
        }

        // Top left triangle
        Box(Modifier.align(Alignment.CenterStart)) {
            Icon(
                modifier = Modifier.size(12.dp),
                painter = painterResource(R.drawable.ic_function),
                tint = Color.DarkGray,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun FunctionContentPreview() {
    BasicSpreadsheetTheme {
        Box(
            Modifier
                .background(Color.LightGray)
                .padding(16.dp)
        ) {
            FunctionContent(
                cell = Cell(
                    uid = "cell1",
                    sheetUid = "sheet1",
                    rowIndex = 0,
                    columnIndex = 0,
                    value = "10",
                    formula = "=SUM(A2:A10)",
                    style = Style(
                        color = Color.White.toArgb(),
                        bold = true,
                        align = Style.Align.RIGHT
                    ).toJson(),
                ),
                result = Result.success("55")
            )
        }
    }
}