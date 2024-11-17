@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.books.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.books.R
import com.darekbx.books.data.model.Book
import com.darekbx.books.data.model.Flags
import com.darekbx.books.ui.LocalColors
import com.darekbx.common.ui.theme.HomeTheme

@Composable
fun BookDialog(
    openedBook: Book? = null,
    listViewModel: ListViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        DialogContents(
            book = openedBook,
            onAdd = { book ->
                if (openedBook != null) {
                    book.id = openedBook.id
                    listViewModel.update(book)
                } else {
                    listViewModel.add(book)
                }
                onDismiss()
            },
            onCancel = { onDismiss() }
        )
    }
}

@Composable
private fun DialogContents(
    book: Book?,
    onAdd: (Book) -> Unit = { },
    onCancel: () -> Unit = { }
) {
    val author = remember { mutableStateOf(book?.author ?: "") }
    val title = remember { mutableStateOf(book?.title ?: "") }

    val kindle = remember { mutableStateOf(book?.isFromKindle() ?: false) }
    val best = remember { mutableStateOf(book?.isBest() ?: false) }
    val good = remember { mutableStateOf(book?.isGood() ?: false) }
    val english = remember { mutableStateOf(book?.isInEnglish() ?: false) }

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
                InputField(Modifier, "Author", author, authorError)
                Spacer(modifier = Modifier.height(8.dp))
                InputField(Modifier, "Title", title, titleError)
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.fillMaxWidth(0.5F)) {
                    FlagCheckBox("Kindle", kindle)
                    FlagCheckBox("Good", good)
                }
                Column(Modifier.fillMaxWidth(1F)) {
                    FlagCheckBox("Best", best)
                    FlagCheckBox("Is English", english)
                }
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

                    val flagsString = Flags.computeFlags(kindle, good, best, english)
                    onAdd(Book(null, author.value, title.value, flagsString))
                }
                CancelButton(onCancel)
            }
        }
    }
}

@Composable
fun CancelButton(onCancel: () -> Unit) {
    Button(
        modifier = Modifier
            .padding(start = 8.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFEC706B)),
        shape = RectangleShape,
        contentPadding = PaddingValues(0.dp),
        onClick = { onCancel() }) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "cancel"
            )
            Spacer(modifier = Modifier.height(4.dp))
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color(0xFFE75B52))
            )
        }
    }
}

@Composable
fun AddButton(onAdd: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth(0.5F),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF75C1BB)),
        shape = RectangleShape,
        contentPadding = PaddingValues(0.dp),
        onClick = { onAdd() }) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = "save"
            )
            Spacer(modifier = Modifier.height(4.dp))
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color(0xFF59918D))
            )
        }
    }
}

@Composable
private fun FlagCheckBox(label: String, isChecked: MutableState<Boolean>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = isChecked.value,
            onCheckedChange = { isChecked.value = it })
        Text(text = label, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    label: String,
    value: MutableState<String>,
    valueError: MutableState<Boolean>
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .background(Color.White),
        value = value.value,
        isError = valueError.value,
        colors = TextFieldDefaults.colors(
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        onValueChange = {
            if (valueError.value) {
                valueError.value = false
            }
            value.value = it
        },
        label = { Text(label) },
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
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
        text = "Add book"
    )
}

@Preview
@Composable
fun DialogPreview() {
    HomeTheme {
        DialogContents(book = null)
    }
}