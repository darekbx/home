package com.darekbx.vault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PinScreen(vaultViewModel: VaultViewModel = hiltViewModel(), onSuccess: () -> Unit = { }) {
    val one = remember { mutableStateOf("") }
    val two = remember { mutableStateOf("") }
    val three = remember { mutableStateOf("") }
    val four = remember { mutableStateOf("") }
    val validationError = remember { mutableStateOf(false) }
    var validationResult by remember { mutableStateOf(null as Boolean?) }

    if (four.value.isNotBlank()) {
        val pin = "${one.value}${two.value}${three.value}${four.value}"
        validationResult = vaultViewModel.validatePin(pin)

        if (validationResult == false) {
            one.value = ""
            two.value = ""
            three.value = ""
            four.value = ""
            validationError.value = true
        } else {
            vaultViewModel.persistPin(pin)
            onSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Enter PIN")
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                InputField(value = one, validationError)
                InputField(value = two, validationError)
                InputField(value = three, validationError)
                InputField(value = four, validationError)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InputField(value: MutableState<String>, hasError: MutableState<Boolean>) {
    val focusManager = LocalFocusManager.current
    TextField(
        modifier = Modifier
            .size(64.dp)
            .padding(4.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .background(Color.White),
        value = value.value,
        isError = hasError.value,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            errorIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Right) }
        ),
        onValueChange = {
            value.value = it
            if (it.length == 1) {
                hasError.value = false
                focusManager.moveFocus(FocusDirection.Right)
            } else {
                focusManager.moveFocus(FocusDirection.Left)
            }
        },
        shape = RoundedCornerShape(8.dp),
        textStyle = TextStyle(fontSize = 18.sp, textAlign = TextAlign.Center),
        singleLine = true
    )
}
