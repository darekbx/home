@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.lifetimememo.screens.container.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.lifetimememo.commonui.CancelIcon
import com.darekbx.lifetimememo.commonui.SaveIcon
import com.darekbx.lifetimememo.commonui.theme.Paddings
import com.darekbx.lifetimememo.screens.memos.viewmodel.MemosViewModel

/**
 * @param parentId Id of the parent container to which new item will be added
 */
@Composable
fun ContainerScreen(
    memosViewModel: MemosViewModel = hiltViewModel(),
    parentId: String? = null,
    onClose: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var titleValid by remember { mutableStateOf(true) }
    var subtitle by remember { mutableStateOf("") }
    var subtitleValid by remember { mutableStateOf(true) }

    val parentContainer = memosViewModel.getContainer(parentId).observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Container") },
                actions = {
                    IconButton(onClick = { onClose() }) { CancelIcon() }
                    IconButton(onClick = {
                        titleValid = true
                        subtitleValid = true
                        if (title.isBlank()) {
                            titleValid = false
                        } else if (subtitle.isBlank()) {
                            subtitleValid = false
                        } else {
                            memosViewModel.add(title, subtitle, parentId)
                            onClose()
                        }
                    }) { SaveIcon() }
                }
            )
        },
        content = { padding ->
            Column(
                Modifier
                    .padding(padding)
                    .padding(top = 8.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                Row(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = "Parent: ")
                    Text(
                        text = "${parentContainer.value?.title ?: "Root"}",
                        fontWeight = FontWeight.Bold
                    )
                }
                TitleField(title, titleValid) { title = it }
                SubtitleField(subtitle, subtitleValid) { subtitle = it }
            }
        }
    )
}

@Composable
private fun TitleField(
    value: String,
    isValid: Boolean,
    onChanged: (String) -> Unit
) {
    TextField(
        modifier = Modifier
            .inputPadding()
            .fillMaxWidth(),
        value = value,
        isError = !isValid,
        onValueChange = { onChanged(it) },
        singleLine = true,
        label = { Text("Title") }
    )
}

@Composable
private fun SubtitleField(
    value: String,
    isValid: Boolean,
    onChanged: (String) -> Unit
) {
    TextField(
        modifier = Modifier
            .inputPadding()
            .fillMaxWidth()
            .height(100.dp),
        value = value,
        isError = !isValid,
        onValueChange = { onChanged(it) },
        singleLine = false,
        maxLines = 3,
        label = { Text("Subtitle") }
    )
}

private fun Modifier.inputPadding() =
    padding(top = Paddings.Default, start = Paddings.Big, end = Paddings.Big)
