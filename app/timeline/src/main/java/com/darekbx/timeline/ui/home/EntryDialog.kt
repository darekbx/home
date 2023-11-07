package com.darekbx.timeline.ui.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.timeline.model.Category
import com.darekbx.timeline.ui.TimeUtils
import com.darekbx.timeline.ui.categories.CategoriesViewModel
import com.darekbx.timeline.ui.categories.CategoryRow
import com.darekbx.timeline.ui.theme.CategoryColors
import com.darekbx.timeline.ui.theme.TimelineTheme
import com.darekbx.timeline.ui.theme.inputColors

@Composable
fun EntryDialog(
    categoriesViewModel: CategoriesViewModel = hiltViewModel(),
    onSave: (Long, String, String, Long) -> Unit,
    onDismiss: () -> Unit
) {
    val categories by categoriesViewModel.categories.collectAsState(initial = emptyList())
    Dialog(onDismissRequest = { onDismiss() }) {
        DialogContents(
            categories = categories,
            onSave = onSave,
            onCancel = onDismiss
        )
    }
}

@Composable
private fun DialogContents(
    categories: List<Category>,
    onSave: (Long, String, String, Long) -> Unit = { _, _, _, _ -> },
    onCancel: () -> Unit = { }
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableLongStateOf(-1L) }
    var selectedTimestamp by remember { mutableLongStateOf(-1L) }

    Card(modifier = Modifier.padding(16.dp), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            DialogTitle()
            TextField(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                value = title,
                onValueChange = { title = it },
                label = { Text(text = "Title") },
                shape = RoundedCornerShape(4.dp),
                colors = inputColors(),
                singleLine = true
            )
            TextField(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                value = description,
                onValueChange = { description = it },
                label = { Text(text = "Decription") },
                shape = RoundedCornerShape(4.dp),
                colors = inputColors(),
                singleLine = true
            )

            CategorySelect(
                modifier = Modifier.padding(bottom = 8.dp),
                categories = categories
            ) { category ->
                selectedCategory = category.id
            }

            DateChooser { timestamp ->
                if (timestamp != null) {
                    selectedTimestamp = timestamp
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
                    if (title.isNotBlank() && selectedCategory != -1L && selectedTimestamp != -1L) {
                        onSave(selectedTimestamp, title, description, selectedTimestamp)
                    } else {
                        Toast
                            .makeText(context, "Please fill all fields!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                    Text(text = "Save")
                }

            }
        }
    }
}

@Composable
private fun CategorySelect(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit = { }
) {
    var name by remember { mutableStateOf("Category") }
    var isCategoryDropDownOpen by remember { mutableStateOf(false) }
    Box(modifier) {
        TextField(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxWidth(),
            value = name,
            readOnly = true,
            enabled = false,
            onValueChange = { name = it },
            shape = RoundedCornerShape(4.dp),
            colors = inputColors(),
            singleLine = true
        )
        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp)
                .clickable { isCategoryDropDownOpen = true },
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = ""
        )
        DropdownMenu(
            modifier = Modifier.fillMaxWidth(0.7F),
            expanded = isCategoryDropDownOpen,
            onDismissRequest = { isCategoryDropDownOpen = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = {
                        CategoryRow(
                            modifier = Modifier.fillMaxWidth(),
                            category = category
                        )
                    }, onClick = {
                        name = category.name
                        isCategoryDropDownOpen = false
                        onCategorySelected(category)
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateChooser(onDateSelected: (Long?) -> Unit) {
    var isDatePickerOpen by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(System.currentTimeMillis())
    val formattedDate by remember {
        derivedStateOf {
            datePickerState.selectedDateMillis
                ?.let { TimeUtils.formattedDate(it) }
                ?: run { "Select date" }
        }
    }
    TextField(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .clickable { isDatePickerOpen = true }
            .fillMaxWidth(),
        value = formattedDate,
        readOnly = true,
        enabled = false,
        onValueChange = { },
        shape = RoundedCornerShape(4.dp),
        colors = inputColors(),
        singleLine = true
    )
    if (isDatePickerOpen) {
        DatePickerDialog(
            onDismissRequest = { isDatePickerOpen = false },
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    isDatePickerOpen = false
                }) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { isDatePickerOpen = false }) {
                    Text(text = "Cancel")
                }
            }) {
            DatePicker(state = datePickerState)
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
        text = "New entry"
    )
}

@Preview
@Composable
fun DialogPreview() {
    TimelineTheme {
        DialogContents(listOf(Category(1L, "Test", CategoryColors[0])))
    }
}
