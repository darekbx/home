@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.dotpad.ui.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.dotpad.R
import com.darekbx.dotpad.ui.CommonLoading
import com.darekbx.dotpad.ui.dots.Dot
import com.darekbx.dotpad.ui.dots.DotSize
import com.darekbx.dotpad.ui.dots.toColor
import com.darekbx.dotpad.ui.theme.*
import com.darekbx.dotpad.utils.TimeUtils

@ExperimentalFoundationApi
@Composable
fun HistoryListScreen(
    dots: State<List<Dot>?>,
    onRestore: (Dot) -> Unit,
    onPermanentlyDelete: (Dot) -> Unit
) {
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    val dotOptionsDialogState = remember { mutableStateOf(false) }
    val selectedDotState = remember { mutableStateOf<Dot?>(null) }
    Column(
        Modifier
            .fillMaxHeight()
            .padding(bottom = 58.dp)
    ) {
        SearchView(textState)
        dots.value?.let { dotsList ->
            DotList(
                dotsList,
                searchState = textState,
                optionsDialogState = dotOptionsDialogState,
                selectedDotState = selectedDotState
            )
        } ?: CommonLoading()
    }

    if (dotOptionsDialogState.value) {
        DotOptionsDialog(
            dotOptionsDialogState,
            selectedDotState.value!!,
            onRestore,
            onPermanentlyDelete
        )
    }
}

@Composable
private fun DotOptionsDialog(
    optionsDialogState: MutableState<Boolean>,
    dot: Dot,
    onRestore: (Dot) -> Unit,
    onPermanentlyDelete: (Dot) -> Unit
) {
    val dotShortText = "\"${dot.text.take(20)}...\""
    AlertDialog(
        containerColor = dialogBackgroud,
        onDismissRequest = { optionsDialogState.value = false },
        confirmButton = {
            Button(onClick = {
                onRestore(dot)
                optionsDialogState.value = false
            }) {
                Text(
                    text = stringResource(id = R.string.action_restore),
                    color = dotTeal.toColor()
                )
            }
        },
        dismissButton = {
            Button(onClick = {
                onPermanentlyDelete(dot)
                optionsDialogState.value = false
            }) {
                Text(
                    text = stringResource(id = R.string.action_delete_permamenty),
                    color = dotRed.toColor()
                )
            }
        },
        text = {
            Text(text = stringResource(id = R.string.select_action, dotShortText))
        }
    )
}

@Composable
private fun DotList(
    dots: List<Dot>,
    searchState: MutableState<TextFieldValue>,
    optionsDialogState: MutableState<Boolean>,
    selectedDotState: MutableState<Dot?>
) {
    val filteredDots = dots.filter {
        it.text.lowercase().contains(searchState.value.text)
    }
    if (filteredDots.isEmpty()) {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Nothing to show",
                style = Typography.headlineSmall
            )
        }
    } else {
        LazyColumn(Modifier.fillMaxWidth()) {
            items(filteredDots) { dot ->
                HistoryDot(dot) {
                    selectedDotState.value = dot
                    optionsDialogState.value = true
                }
                Divider(color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun SearchView(state: MutableState<TextFieldValue>) {
    TextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = state.value,
        onValueChange = { value: TextFieldValue ->
            state.value = value
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value != TextFieldValue("")) {
                IconButton(
                    onClick = {
                        state.value = TextFieldValue("")
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = dotYellow.toColor(),
            //leadingIconColor = Color.White,
            //trailingIconColor = Color.White,
            containerColor = Color.Black
        )
    )
}

@Composable
fun HistoryDot(dot: Dot, onClick: () -> Unit) {
    Column(
        Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(
                        dot
                            .requireColor()
                            .toColor()
                    )
                    .size(15.dp)
            )
            Text(
                text = dot.createdDate.let { TimeUtils.formattedDate(it) },
                style = Typography.titleLarge.copy(color = LightGrey)
            )
        }

        Text(
            text = dot.text,
            style = Typography.headlineSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchViewPreview() {
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    SearchView(textState)
}

@ExperimentalFoundationApi
@Preview
@Composable
fun HistoryDotPreview() {
    HistoryDot(
        Dot(1L, "Test dot content", 0F, 0F, DotSize.MEDIUM, dotTeal)
    ) { }
}
