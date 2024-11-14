package com.darekbx.weight.ui.chart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.weight.data.model.EntryType
import com.darekbx.weight.data.model.WeightEntry
import com.darekbx.weight.ui.WeightViewModel

@Composable
fun NewEntryDialog(
    weightViewModel: WeightViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val weightError = remember { mutableStateOf(false) }
    val weight = remember { mutableStateOf("") }
    val type = remember { mutableStateOf(EntryType.MONIKA) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add new entry") },
        text = { DialogContents(weightError, weight, type) },
        confirmButton = {
            Button(onClick = {
                val weightDouble = weight.value.toDoubleOrNull()
                if (weightDouble != null) {
                    weightViewModel.add(weight.value.toDouble(), type.value)
                    onDismiss()
                } else {
                    weightError.value = true
                }
            }) { Text("Add") }
        },
        dismissButton = { Button(onClick = { onDismiss() }) { Text("Cancel") } }
    )
}

@Composable
private fun DialogContents(
    weightError: MutableState<Boolean>,
    weight: MutableState<String>,
    type: MutableState<EntryType>
) {
    Column {
        CustomField(weightError, weight, "Liters")
        Spacer(modifier = Modifier.height(4.dp))
        TypeCheckbox(type, EntryType.MONIKA)
        TypeCheckbox(type, EntryType.DAREK)
        TypeCheckbox(type, EntryType.MICHAL)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CustomField(
    weightError: MutableState<Boolean>,
    value: MutableState<String>,
    label: String
) {
    Box(contentAlignment = Alignment.CenterStart) {
        val lightBlue = MaterialTheme.colorScheme.primary.copy(alpha = 0.1F)
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value.value,
            isError = weightError.value,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = lightBlue,
                cursorColor = Color.Black,
                disabledLabelColor = lightBlue,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            onValueChange = {
                if (it.isEmpty()) {
                    value.value = "0"
                } else {
                    value.value = it
                }
            },
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}

@Composable
private fun TypeCheckbox(type: MutableState<EntryType>, kind: EntryType) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp),
            checked = type.value == kind,
            onCheckedChange = { type.value = kind })
        Text(
            text = WeightEntry.typeNameFormatted(kind),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewDialogContents() {
    val weightError = remember { mutableStateOf(true) }
    val weight = remember { mutableStateOf("52.2") }
    val type = remember { mutableStateOf(EntryType.DAREK) }
    HomeTheme {
        DialogContents(weightError, weight, type)
    }
}
