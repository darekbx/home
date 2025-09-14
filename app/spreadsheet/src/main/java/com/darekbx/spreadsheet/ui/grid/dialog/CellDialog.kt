package com.darekbx.spreadsheet.ui.grid.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.spreadsheet.R
import com.darekbx.spreadsheet.model.Cell
import com.darekbx.spreadsheet.model.Style
import com.darekbx.spreadsheet.model.Style.Companion.styleFromJson
import com.darekbx.spreadsheet.model.Style.Companion.toJson
import com.darekbx.spreadsheet.ui.grid.functions.Function.Companion.toFunction
import com.darekbx.spreadsheet.ui.theme.BasicSpreadsheetTheme
import com.darekbx.spreadsheet.ui.theme.GROUP_COLORS

@Composable
fun CellDialog(
    cell: Cell,
    onDismiss: () -> Unit,
    onSave: (Cell) -> Unit,
) {
    val colors = remember { GROUP_COLORS.map { it.copy(alpha = 0.5F) } }
    var value by remember { mutableStateOf(cell.value) }
    var formula by remember { mutableStateOf(cell.formula) }
    var isFormulaValid by remember { mutableStateOf(true) }

    val cellStyle = cell.style?.takeIf { it.isNotEmpty() }?.styleFromJson()
    var color by remember { mutableStateOf(cellStyle?.composeColor() ?: colors[0]) }
    var bold by remember { mutableStateOf(cellStyle?.bold == true) }
    var align by remember { mutableStateOf(cellStyle?.align ?: Style.Align.LEFT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Cell ${cell.name}") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Cell Value") },
                    maxLines = 4
                )
                OutlinedTextField(
                    value = formula ?: "",
                    isError = !isFormulaValid,
                    onValueChange = { formula = it },
                    label = { Text("Formula") },
                    maxLines = 1
                )

                ColorsRow(color, colors, onColorChange = { color = it })
                BoldRow(bold, onBoldChange = { bold = it })
                AlignRow(align, onAlignChange = { align = it })
            }
        },
        confirmButton = {
            Button(onClick = {
                val formulaFunction = formula.toFunction()
                if (formulaFunction.isFailure) {
                    isFormulaValid = false
                } else {
                    onSave(
                        cell.copy(
                            value = value,
                            formula = formula,
                            style = Style(color = color.toArgb(), bold = bold, align = align)
                                .toJson(),
                        )
                    )
                    onDismiss()
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AlignRow(
    align: Style.Align,
    onAlignChange: (Style.Align) -> Unit = {}
) {
    val alignments = mapOf(
        Style.Align.LEFT to R.drawable.ic_align_left,
        Style.Align.CENTER to R.drawable.ic_align_center,
        Style.Align.RIGHT to R.drawable.ic_align_right
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Align")
        alignments.forEach {
            val selectedModifier = if (it.key == align) {
                Modifier.border(2.dp, Color.Black, RectangleShape)
            } else {
                Modifier
            }
            Box(
                modifier = Modifier
                    .clickable { onAlignChange(it.key) }
                    .then(selectedModifier)
                    .padding(2.dp)
            ) {
                Icon(painter = painterResource(it.value), contentDescription = it.key.name)
            }
        }
    }
}

@Composable
private fun BoldRow(
    isBold: Boolean,
    onBoldChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable { onBoldChange(!isBold) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Is bold")
        Checkbox(checked = isBold, onCheckedChange = { onBoldChange(!isBold) })
    }
}

@Composable
private fun ColorsRow(
    color: Color,
    colors: List<Color>,
    onColorChange: (Color) -> Unit = { }
) {
    FlowRow(
        modifier = Modifier.padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        colors.forEach {
            val selectedModifier = if (it == color) {
                Modifier.border(2.dp, Color.Black, CircleShape)
            } else {
                Modifier.border(0.5.dp, Color.Black, CircleShape)
            }
            Spacer(
                modifier = Modifier
                    .size(32.dp)
                    .background(it, CircleShape)
                    .clip(CircleShape)
                    .clickable { onColorChange(it) }
                    .then(selectedModifier)
            )
        }
    }
}

@Preview
@Composable
fun CellDialogPreview() {
    BasicSpreadsheetTheme {
        CellDialog(
            cell = Cell(
                uid = "",
                rowIndex = 0,
                columnIndex = 0,
                value = "Sample Value",
                sheetUid = "1"
            ),
            onDismiss = {},
            onSave = { updatedCell -> }
        )
    }
}
