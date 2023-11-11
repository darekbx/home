package com.darekbx.timeline.ui.categories

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.darekbx.timeline.ui.theme.CategoryColors
import com.darekbx.timeline.ui.theme.TimelineTheme
import com.darekbx.timeline.ui.theme.inputColors

@Composable
fun CategoryDialog(
    usedColors: List<Int>,
    onSave: (String, Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        DialogContents(
            usedColors = usedColors,
            onSave = onSave,
            onCancel = onDismiss
        )
    }
}

@Composable
private fun DialogContents(
    usedColors: List<Int>,
    onSave: (String, Int) -> Unit = { _, _ -> },
    onCancel: () -> Unit = { }
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableIntStateOf(0) }
    val selectedColorModifier = Modifier.border(2.dp, Color.White, RoundedCornerShape(32.dp))

    Card(modifier = Modifier.padding(16.dp), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            DialogTitle()
            TextField(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                value = name,
                onValueChange = { name = it },
                shape = RoundedCornerShape(4.dp),
                colors = inputColors(),
                singleLine = true
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                itemsIndexed(CategoryColors) { index, color ->
                    Spacer(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(32.dp)
                            .then(
                                if (usedColors.contains(color)) Modifier.alpha(0.0F)
                                else Modifier.clickable { selectedColor = index }
                            )
                            .clip(CircleShape)
                            .background(Color(color))
                            .then(if (selectedColor == index) selectedColorModifier else Modifier)
                    )
                }
            }

            Row(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onCancel) { Text(text = "Cancel") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, CategoryColors[selectedColor])
                        onCancel()
                    }
                }) {
                    Text(text = "Save")
                }

            }
        }
    }
}

@Composable
private fun DialogTitle() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        text = "New category"
    )
}

@Preview
@Composable
fun DialogPreview() {
    TimelineTheme {
        DialogContents(listOf(CategoryColors[2], CategoryColors[4], CategoryColors[6]))
    }
}
