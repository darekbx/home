package com.darekbx.lifetimememo.screens.memos.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.lifetimememo.commonui.ElementsList
import com.darekbx.lifetimememo.commonui.ProgressIndicator
import com.darekbx.lifetimememo.commonui.theme.*
import com.darekbx.lifetimememo.screens.memos.MemosUiState
import com.darekbx.lifetimememo.screens.memos.model.Memo
import com.darekbx.lifetimememo.screens.memos.viewmodel.MemosViewModel

@Composable
fun MemosScreen(
    memosViewModel: MemosViewModel = hiltViewModel(),
    parentId: String? = null,
    onMemoClick: (id: String?) -> Unit,
    onContainerClick: (id: String?) -> Unit,
    onAddMemoClick: (parentId: String?) -> Unit,
    onAddContainerClick: (parentId: String?) -> Unit,
) {
    val context = LocalContext.current
    val elements by memosViewModel.elements(parentId).observeAsState(emptyList())
    val uiState by memosViewModel.uiState.observeAsState()
    var path by remember { mutableStateOf<List<String>>(emptyList()) }
    var confirmationDialogShow by remember { mutableStateOf(false) }
    var memoToDelete by remember { mutableStateOf<Memo?>(null) }

    LaunchedEffect(parentId) {
        path = memosViewModel.getPath(parentId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            CurrentLocation(path)
            ElementsList(
                elements,
                onContainerClick,
                onMemoClick,
                onMemoLongPress = { memo ->
                    memoToDelete = memo
                    confirmationDialogShow = true
                },
                onContainerLongPress = { container ->
                    if (container.childrenCount > 0) {
                        cantDeleteContainerToast(context)
                    } else {
                        memosViewModel.delete(container)
                    }
                }
            )
        }

        ActionButtons(parentId, onAddMemoClick, onAddContainerClick)

        if (uiState is MemosUiState.InProgress) {
            ProgressIndicator()
        }

        if (confirmationDialogShow && memoToDelete != null) {
            DeleteConfirmationDialog(memoToDelete, memosViewModel) {
                confirmationDialogShow = false
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    memoToDelete: Memo?,
    memosViewModel: MemosViewModel,
    onDismiss: () -> Unit = { }
) {
    val message = buildAnnotatedString {
        append("Delete ")
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(memoToDelete!!.title)
        }
        append(" memo?")
    }
    ConfirmationDialog(
        message = message,
        confirmButtonText = "Delete",
        onDismiss = { onDismiss() },
        onConfirm = { memosViewModel.delete(memoToDelete!!) }
    )
}

@Composable
private fun ActionButtons(
    parentId: String?,
    onAddMemoClick: (parentId: String?) -> Unit,
    onAddContainerClick: (parentId: String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        ExtendedFloatingActionButton(
            onClick = { onAddMemoClick(parentId) },
            icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") },
            text = { Text(text = "Memo") },
            shape = CircleShape
        )
        Spacer(modifier = Modifier.height(16.dp))
        ExtendedFloatingActionButton(
            onClick = { onAddContainerClick(parentId) },
            icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") },
            text = { Text(text = "Container") },
            shape = CircleShape
        )
    }
}

@Composable
private fun CurrentLocation(path: List<String>) {
    Card(
        modifier = Modifier
            .padding(start = 12.dp, top = 12.dp, end = 12.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkGreen.copy(alpha = 0.6F))
    ) {
        val annotetedString = buildAnnotatedString {
            when {
                path.isEmpty() -> append("\\")
                path.size == 1 -> append("\\${path.first()}")
                else -> {
                    append("\\${path.dropLast(1).joinToString("\\")}")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                        append("\\${path.last()}")
                    }
                }
            }
        }
        Text(
            modifier = Modifier.padding(8.dp),
            text = annotetedString,
            fontSize = 13.sp
        )
    }
}

private fun cantDeleteContainerToast(context: Context) {
    Toast.makeText(context, "Cannot delete not empty Container!", Toast.LENGTH_LONG).show()
}
