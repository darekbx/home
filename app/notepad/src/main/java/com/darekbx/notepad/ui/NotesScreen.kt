@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.notepad.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NotesScreen(notepadViewModel: NotepadViewModel = hiltViewModel()) {

    val notes by notepadViewModel.getNotes().collectAsState(initial = emptyList())
    if (notes.isNotEmpty()) {

        val noteOneContent = remember { mutableStateOf(notes[0].contents) }
        val noteTwoContent = remember { mutableStateOf(notes[1].contents) }

        DisposableEffect(Unit) {
            onDispose {
                if (noteOneContent.value.isNotEmpty()) {
                    notepadViewModel.updateNote(notes[0].id, noteOneContent.value)
                }
                if (noteTwoContent.value.isNotEmpty()) {
                    notepadViewModel.updateNote(notes[1].id, noteTwoContent.value)
                }
            }
        }

        NotesView(noteOneContent, noteTwoContent)
    }
}

@OptIn(ExperimentalUnitApi::class)
@ExperimentalMaterial3Api
@Composable
private fun NotesView(
    noteOneContent: MutableState<String>,
    noteTwoContent: MutableState<String>
) {
    var activeNote by remember { mutableStateOf(0) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            NoteButton(
                modifier = Modifier
                    .fillMaxWidth(0.5F)
                    .padding(start = 4.dp, top = 2.dp, end = 4.dp),
                text = "A",
                isActive = activeNote == 0
            ) { activeNote = 0 }
            NoteButton(
                modifier = Modifier
                    .fillMaxWidth(1F)
                    .padding(top = 2.dp, end = 4.dp),
                text = "B",
                isActive = activeNote == 1
            ) { activeNote = 1 }
        }
        TextField(
            modifier = Modifier
                .fillMaxWidth(1F)
                .fillMaxHeight(1F)
                .padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
            shape = RoundedCornerShape(2.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(252, 245, 181),
                cursorColor = Color(11, 23, 125),
                textColor = Color(11, 23, 125),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
                letterSpacing = TextUnit(1.05F, TextUnitType.Sp),
                lineHeight = TextUnit(1.2F, TextUnitType.Em)
            ),
            singleLine = false,
            value = if (activeNote == 0) noteOneContent.value else noteTwoContent.value,
            onValueChange = {
                if (activeNote == 0) noteOneContent.value = it
                else noteTwoContent.value = it
            }
        )
    }
}

@Composable
private fun NoteButton(modifier: Modifier, text: String, isActive: Boolean, onClick: () -> Unit) {
    ElevatedButton(
        modifier = modifier
            .alpha(if (isActive) 0.5F else 1F),
        shape = RoundedCornerShape(2.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(242, 235, 171)),
        onClick = { onClick() }
    ) {
        Text(text = text, color = Color.Black)
    }
}
