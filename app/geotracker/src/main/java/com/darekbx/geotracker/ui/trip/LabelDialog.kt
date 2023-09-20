package com.darekbx.geotracker.ui.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.geotracker.ui.theme.LocalStyles

@Composable
fun LabelDialog(
    label: String?,
    title: String,
    onSave: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        DialogContents(
            labelValue = label,
            title = title,
            onSave = onSave,
            onCancel = onDismiss
        )
    }
}

@Composable
private fun DialogContents(
    labelValue: String?,
    title: String,
    onSave: (String?) -> Unit = { },
    onCancel: () -> Unit = { }
) {
    val label = remember { mutableStateOf(labelValue) }
    Card(modifier = Modifier.padding(16.dp), shape = RoundedCornerShape(16.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            DialogTitle(title)
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                InputField(Modifier, label)
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    if (label.value.isNullOrEmpty()) {
                        onSave(null)
                    } else {
                        onSave(label.value)
                    }
                }) {
                    Text(text = "Save")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onCancel) {
                    Text(text = "Cancel")
                }
            }
        }
    }
}

@Composable
private fun InputField(
    modifier: Modifier = Modifier,
    value: MutableState<String?>
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value.value ?: "",
        onValueChange = { value.value = it },
        singleLine = true
    )
}

@Composable
private fun DialogTitle(title: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        style = LocalStyles.current.grayLabel,
        fontSize = 18.sp,
        text = title
    )
}

@Preview
@Composable
fun DialogPreview() {
    HomeTheme {
        DialogContents("Label", "Trip label")
    }
}