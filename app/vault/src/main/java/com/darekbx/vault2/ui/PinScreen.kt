package com.darekbx.vault2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.vault.ui.VaultViewModel

private enum class PinButton(val value: Int) {
    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), ZERO(0)
}

private enum class PinButtonAction(val value: Char) {
    BACK('<'), DONE('X')
}

@Composable
fun PinScreen(vaultViewModel: VaultViewModel = hiltViewModel(), onSuccess: () -> Unit = { }) {
    PinScreen { pin ->
        val pinString = pin.joinToString(separator = "")
        val result = vaultViewModel.validatePin(pinString)
        if (result) {
            vaultViewModel.persistPin(pinString)
            onSuccess()
        }
        result
    }
}

@Composable
private fun PinScreen(onValidate: (List<Int>) -> Boolean = { false }) {
    val buttons = remember {
        listOf(
            listOf(PinButton.ONE, PinButton.TWO, PinButton.THREE),
            listOf(PinButton.FOUR, PinButton.FIVE, PinButton.SIX),
            listOf(PinButton.SEVEN, PinButton.EIGHT, PinButton.NINE),
            listOf(PinButtonAction.BACK, PinButton.ZERO, PinButtonAction.DONE)
        )
    }
    var isValid by remember { mutableStateOf(false) }
    val pin = remember { mutableStateListOf<Int>() }
    val isEnabled = pin.size < 4

    Column(Modifier.padding(start = 38.dp, end = 38.dp, bottom = 8.dp, top = 8.dp)) {
        Icon(
            modifier = Modifier
                .padding(32.dp)
                .size(80.dp)
                .align(Alignment.CenterHorizontally),
            imageVector = Icons.Default.Lock,
            contentDescription = "Lock"
        )

        Spacer(modifier = Modifier.weight(1F))

        Text(
            text = "Enter PIN",
            fontSize = 24.sp,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .align(Alignment.CenterHorizontally)
        )
        Row(Modifier.align(Alignment.CenterHorizontally)) {
            PinDigit(filled = pin.elementAtOrNull(0) != null)
            PinDigit(filled = pin.elementAtOrNull(1) != null)
            PinDigit(filled = pin.elementAtOrNull(2) != null)
            PinDigit(filled = pin.elementAtOrNull(3) != null)
        }

        Spacer(modifier = Modifier.weight(1F))

        Column(Modifier.padding(top = 16.dp)) {
            buttons.forEach { group ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    group.forEach { button ->
                        when (button) {
                            is PinButton -> {
                                KeyButton(button.value, isEnabled) { digit ->
                                    pin.add(digit)
                                }
                            }
                            is PinButtonAction -> {
                                KeyButton(button.value, true) {
                                    when (button.value) {
                                        '<' -> pin.removeLastOrNull()
                                        'X' -> {
                                            isValid = onValidate(pin)
                                            if (!isValid) pin.clear()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.KeyButton(char: Any, enabled: Boolean, onClick: (Int) -> Unit = {}) {
    ElevatedButton(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
            .padding(12.dp)
            .aspectRatio(1F),
        enabled = enabled,
        onClick = {
            if (char is Int) onClick(char)
            else onClick(0)
        }
    ) {
        when (char) {
            'X' -> Icon(Icons.Default.Done, contentDescription = "Done")
            '<' -> Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            else -> Text(char.toString(), fontSize = 24.sp)
        }
    }
}

@Composable
private fun PinDigit(filled: Boolean = false) {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .size(56.dp)
            .background(Color.White, CircleShape)
            .innerShadow(CircleShape, Color.Gray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .background(
                    if (filled) MaterialTheme.colorScheme.primary else Color.Transparent,
                    CircleShape
                )
        )
    }
}

@Preview(device = Devices.PIXEL_6A)
@Composable
fun PinScreenPreview() {
    HomeTheme(isDarkTheme = false) {
        Surface(Modifier.fillMaxSize()) {
            PinScreen()
        }
    }
}
