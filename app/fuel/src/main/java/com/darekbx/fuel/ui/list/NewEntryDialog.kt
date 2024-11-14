@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.darekbx.fuel.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.fuel.model.FuelType

@Composable
fun NewEntryDialog(
    fuelViewModel: FuelViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val liters = remember { mutableStateOf<String?>(null) }
    val price = remember { mutableStateOf<String?>(null) }
    val type = remember { mutableStateOf(FuelType.PB95) }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add new entry") },
        text = { DialogContents(liters, price, type) },
        confirmButton = {
            Button(onClick = {
                liters.value?.let { liters ->
                    price.value?.let { price ->
                        fuelViewModel.add(liters.toDouble(), price.toDouble(), type.value)
                        onDismiss()
                    } ?: run { /* TODO validation */ }
                } ?: run { /* TODO validation */ }
            }) { Text("Add") }
        },
        dismissButton = { Button(onClick = { onDismiss() }) { Text("Cancel") } }
    )
}

@Composable
private fun DialogContents(
    liters: MutableState<String?>,
    price: MutableState<String?>,
    type: MutableState<FuelType>
) {
    Column {
        CustomField(liters, "Liters")
        Spacer(modifier = Modifier.height(4.dp))
        CustomField(price, "Price")
        TypeCheckbox(type, FuelType.PB95, "Gasoline")
        TypeCheckbox(type, FuelType.ON, "Diesel")
    }
}

@Composable
private fun TypeCheckbox(type: MutableState<FuelType>, kind: FuelType, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = kind.icon()),
            contentDescription = "type"
        )
        Checkbox(
            modifier = Modifier.padding(8.dp),
            checked = type.value == kind,
            onCheckedChange = { type.value = kind })
        Text(text = label, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CustomField(value: MutableState<String?>, label: String) {
    Box(contentAlignment = Alignment.CenterStart) {
        val lightBlue = MaterialTheme.colorScheme.primary.copy(alpha = 0.1F)
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value.value ?: "",
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
                    value.value = null
                } else {
                    value.value = it
                }
            },
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
        if (value.value == null) {
            Text(
                text = label,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                textAlign = TextAlign.Start,
                color = Color.DarkGray
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewDialogContents() {
    val liters = remember { mutableStateOf<String?>(null) }
    val price = remember { mutableStateOf<String?>(null) }
    val type = remember { mutableStateOf(FuelType.PB95) }
    HomeTheme {
        DialogContents(liters, price, type)
    }
}

