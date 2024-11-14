package com.darekbx.geotracker.ui.settings

import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.geotracker.ui.theme.LocalStyles
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ManualTripDialog(
    onSave: (Float, Long, Long) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        DialogContents(
            onSave = onSave,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun DialogContents(
    onSave: (Float, Long, Long) -> Unit = { _, _, _ -> },
    onDismiss: () -> Unit = { }
) {
    val dateFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    fun formattedDate(timestamp: Long) = dateFormatter.format(Date(timestamp))

    val distance = remember { mutableStateOf<Float?>(null) }
    val start = remember { mutableStateOf<Long?>(null) }
    val end = remember { mutableStateOf<Long?>(null) }
    val startFormatted = remember { mutableStateOf<String?>(null) }
    val endFormatted = remember { mutableStateOf<String?>(null) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Card(modifier = Modifier.padding(16.dp), shape = RoundedCornerShape(16.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            DialogTitle("Add new trip")
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                InputField(Modifier, "Distance (km)", distance)
                ReadOnlyInputField(Modifier.clickable {
                    showStartTimePicker = true
                }, "Start time", startFormatted)
                ReadOnlyInputField(Modifier.clickable {
                    showEndTimePicker = true
                }, "End time", endFormatted)
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    val dist = distance.value
                    val startTime = start.value
                    val endTime = end.value
                    if (dist != null && startTime != null && endTime != null) {
                        onSave(dist, startTime, endTime)
                        onDismiss()
                    }
                }) {
                    Text(text = "Save")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
            }
        }
    }

    if (showStartTimePicker) {
        ShowTimePicker { hour, minute ->
            showStartTimePicker = false
            start.value = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }.timeInMillis
            start.value?.let {
                startFormatted.value = formattedDate(it)
            }
        }
    }
    if (showEndTimePicker) {
        ShowTimePicker { hour, minute ->
            showEndTimePicker = false
            end.value = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }.timeInMillis
            end.value?.let {
                endFormatted.value = formattedDate(it)
            }
        }
    }
}

@Composable
private fun ShowTimePicker(timeCallback: (Int, Int) -> Unit) {
    val now = Calendar.getInstance()
    val picker = TimePickerDialog(
        LocalContext.current,
        { _: TimePicker, hour: Int, minute: Int ->
            timeCallback(hour, minute)
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true
    )
    picker.show()
}

@Composable
private fun InputField(
    modifier: Modifier = Modifier,
    label: String,
    value: MutableState<Float?>
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = "${value.value ?: ""}",
        label = { Text(text = label) },
        onValueChange = { value.value = it.toFloatOrNull() },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}

@Composable
private fun ReadOnlyInputField(
    modifier: Modifier = Modifier,
    label: String,
    value: MutableState<String?>
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value.value ?: "",
        label = { Text(text = label) },
        enabled = false,
        onValueChange = { },
    )
}

@Composable
private fun DialogTitle(title: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        style = LocalStyles.current.grayLabel,
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        text = title
    )
}

@Preview
@Composable
fun DialogPreview() {
    HomeTheme {
        DialogContents()
    }
}