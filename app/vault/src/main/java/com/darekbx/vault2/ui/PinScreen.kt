package com.darekbx.vault2.ui

import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.vault.ui.VaultViewModel
import kotlin.math.truncate

@Composable
fun PinScreen(vaultViewModel: VaultViewModel = hiltViewModel(), onSuccess: () -> Unit = { }) {

}

@Composable
fun PinScreen(onValidate: (Array<Int>) -> Boolean = { false }) {
    Column(Modifier.padding(32.dp)) {
        Icon(
            modifier = Modifier
                .padding(48.dp)
                .size(96.dp)
                .align(Alignment.CenterHorizontally),
            imageVector = Icons.Default.Lock,
            contentDescription = "Lock"
        )
        Text(
            text = "Enter PIN",
            fontSize = 28.sp,
            modifier = Modifier
                .padding(top = 48.dp, bottom = 32.dp)
                .align(Alignment.CenterHorizontally)
        )

        Row(Modifier.align(Alignment.CenterHorizontally)) {
            PinDigit(filled = true)
            PinDigit(filled = true)
            PinDigit(filled = false)
            PinDigit(filled = false)
        }
    }
}

@Composable
fun PinDigit(filled: Boolean = false) {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .size(64.dp)
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
