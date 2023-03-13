package com.darekbx.books.ui.toread

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.books.data.model.ToRead
import com.darekbx.books.ui.LocalColors
import com.darekbx.books.ui.list.AddButton
import com.darekbx.books.ui.list.CancelButton
import com.darekbx.books.ui.list.InputField
import com.darekbx.common.ui.theme.HomeTheme

@Composable
fun ToReadDialog(
    toReadViewModel: ToReadViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        DialogContents(
            onAdd = { toRead ->
                toReadViewModel.add(toRead)
                onDismiss()
            },
            onCancel = { onDismiss() }
        )
    }
}

@Composable
private fun DialogContents(
    onAdd: (ToRead) -> Unit = { },
    onCancel: () -> Unit = { }
) {
    val author = remember { mutableStateOf("") }
    val title = remember { mutableStateOf("") }

    val authorError = remember { mutableStateOf(false) }
    val titleError = remember { mutableStateOf(false) }

    Card(modifier = Modifier.padding(16.dp), shape = RoundedCornerShape(16.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            DialogTitle()
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                InputField("Author", author, authorError)
                Spacer(modifier = Modifier.height(8.dp))
                InputField("Title", title, titleError)
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEDEDE6))
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            ) {
                AddButton {
                    if (author.value.isBlank()) {
                        authorError.value = true
                        return@AddButton
                    }

                    authorError.value = false

                    if (title.value.isBlank()) {
                        titleError.value = true
                        return@AddButton
                    }

                    titleError.value = false

                    onAdd(ToRead(null, author.value, title.value))
                }
                CancelButton(onCancel)
            }
        }
    }
}

@Composable
private fun DialogTitle() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColors.current.green)
            .padding(16.dp),
        textAlign = TextAlign.Center,
        color = Color.White,
        fontWeight = FontWeight.W600,
        text = "To read"
    )
}

@Preview
@Composable
fun DialogPreview() {
    HomeTheme {
        DialogContents()
    }
}