@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.darekbx.books.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.books.data.model.Book
import com.darekbx.books.ui.LocalColors
import com.darekbx.common.ui.ConfirmationDialog
import com.darekbx.common.ui.theme.HomeTheme

@Composable
fun ListScreen(listViewModel: ListViewModel = hiltViewModel()) {
    val books by listViewModel.books().collectAsState(initial = emptyList())
    var deleteBookDialog by remember { mutableStateOf(false) }
    var showBookDialog by remember { mutableStateOf(false) }
    var clickedBook by remember { mutableStateOf(null as Book?) }
    val count by remember {
        derivedStateOf { books.size }
    }

    /*
    // Uncomment to fill data from legacy db
    LaunchedEffect(Unit) {
        listViewModel.fillData()
    }
     */

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Books ($count)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    titleContentColor = Color.White,
                    containerColor = LocalColors.current.green
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showBookDialog = true }, shape = CircleShape) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                BooksList(
                    books,
                    count,
                    onBookClick = {
                        clickedBook = it
                        showBookDialog = true
                    },
                    onBookLongClick = {
                        clickedBook = it
                        deleteBookDialog = true
                    }
                )

                if (showBookDialog) {
                    BookDialog(clickedBook) {
                        clickedBook = null
                        showBookDialog = false
                    }
                }

                if (deleteBookDialog) {
                    ConfirmationDialog(
                        message = "Delete ${clickedBook?.author} ${clickedBook?.title}?",
                        confirmButtonText = "Delete",
                        onDismiss = {
                            clickedBook = null
                            deleteBookDialog = false
                        },
                        onConfirm = { listViewModel.delete(clickedBook) }
                    )
                }
            }
        },
    )
}

@Composable
private fun BooksList(
    books: List<Book>,
    count: Int,
    onBookClick: (Book) -> Unit,
    onBookLongClick: (Book) -> Unit
) {
    var filter = remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        stickyHeader {
            InputField(
                modifier = Modifier.padding(start = 2.dp, end = 2.dp, top = 2.dp, bottom = 4.dp),
                value = filter,
                label = "Search",
                valueError = mutableStateOf(false)
            )
        }
        itemsIndexed(books.filter {
            if (filter.value.isNotBlank()) {
                it.title.contains(filter.value, true) || it.author.contains(filter.value, true)
            } else {
                true
            }
        }, key = { _, book -> book.id!! }) { index, book ->
            BookEntryView(
                book,
                count - index,
                onClick = { onBookClick(book) },
                onLongClick = { onBookLongClick(book) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookEntryView(book: Book, index: Int, onClick: () -> Unit, onLongClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 0.dp, bottom = 0.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            FlagMark(if (book.isFromKindle()) Color(0xFF8FC162) else Color.Transparent)
            FlagMark(
                when {
                    book.isGood() -> Color(0xFF44938E)
                    book.isBest() -> Color(0xFFFF4D4B)
                    else -> Color.Transparent
                }
            )
            FlagMark(if (book.isInEnglish()) Color(0xFF0A247D) else Color.Transparent)
        }
        Row(Modifier.fillMaxWidth(0.98F), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 4.dp, top = 4.dp)
                    .height(41.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1F))
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 4.dp, top = 4.dp)
                    .height(41.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${index}.",
                    style = MaterialTheme.typography.labelSmall,
                )
                Spacer(modifier = Modifier.weight(1F))
                Text(
                    text = "${book.year}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun FlagMark(color: Color) {
    Spacer(
        modifier = Modifier
            .width(4.dp)
            .height(50.dp)
            .background(color)
    )
}

@Preview(showSystemUi = true)
@Composable
fun BookEntryViewPreview() {
    HomeTheme {
        BookEntryView(Book(1, "Stephen King", "Carrie", "023", 2018), 437, { }, { })
    }
}
