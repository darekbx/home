@file:OptIn(ExperimentalPermissionsApi::class)

package com.darekbx.dotpad.ui.dots

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.darekbx.dotpad.R
import com.darekbx.dotpad.ui.theme.*
import com.darekbx.dotpad.utils.RequestPermission
import com.darekbx.dotpad.utils.TimeUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi

private val colors = listOf(
    dotRed,
    dotTeal,
    dotBlue,
    dotPurple,
    dotOrange,
    dotYellow
)

@Composable
fun DotDialog(
    dot: Dot,
    onSave: (Dot) -> Unit,
    onResetTime: (Dot) -> Unit,
    onShowDatePicker: () -> Unit,
    onRemove: (Dot) -> Unit,
    onDiscardChanges: () -> Unit
) {

    RequestPermission(Manifest.permission.WRITE_CALENDAR) {
        Dialog(
            onDismissRequest = { onDiscardChanges() },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            DialogContent(dot, onSave, onResetTime, onShowDatePicker, onRemove)
        }
    }
}

@Composable
fun DialogContent(
    dot: Dot,
    onSave: (Dot) -> Unit,
    onResetTime: (Dot) -> Unit,
    onShowDatePicker: () -> Unit,
    onRemove: (Dot) -> Unit
) {

    val (text, onTextChange) = rememberSaveable { mutableStateOf(dot.text) }
    val (size, onSizeChange) = rememberSaveable { mutableStateOf(dot.size) }
    val (color, onColorChange) = rememberSaveable { mutableStateOf(dot.color) }
    val (isSticked, onStickedChange) = rememberSaveable { mutableStateOf(dot.isSticked) }

    val submit = {
        dot.text = text
        dot.size = size
        dot.color = color
        dot.isSticked = isSticked
        onSave(dot)
    }

    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(382.dp)
                .background(dialogBackgroud)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                DotMessage(text, onTextChange)

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReminderInfo(dot)
                    StickedInfo(isSticked, onStickedChange)
                }

                DotColors(color, onColorChange)
                DotSizes(size, onSizeChange)

                if (dot.isNew) {
                    NewDotButtons(submit, onShowDatePicker)
                } else {
                    EditDotButtons(submit, onResetTime, onShowDatePicker, onRemove, dot)
                }
            }
        }
    }
}

@Composable
private fun NewDotButtons(
    onSave: () -> Unit,
    onAddReminder: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Button(
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth(0.85F)
                .padding(end = 4.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = dotTeal.toColor()),
            onClick = { onSave() }) { Text(text = "") }
        SquareButton({ onAddReminder() }, dotPurple)
    }
}

@Composable
private fun EditDotButtons(
    onSave: () -> Unit,
    onResetTime: (Dot) -> Unit,
    onAddReminder: () -> Unit,
    onRemove: (Dot) -> Unit,
    dot: Dot
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SquareButton({ onRemove(dot) }, dotRed)
        SquareButton({ onSave() }, dotTeal, width = 140.dp)
        SquareButton({ onResetTime(dot) }, dotOrange)
        SquareButton({ onAddReminder() }, dotPurple)
    }
}

@Composable
private fun SquareButton(
    onClick: () -> Unit,
    color: DotColor,
    width: Dp = 44.dp
) {
    Button(
        modifier = Modifier
            .height(44.dp)
            .width(width),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color.toColor()),
        onClick = { onClick() }) { }
}

@Composable
private fun DotColors(dotColor: DotColor?, onColorChange: (DotColor) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onColorChange(color) }
                    .background(color.toColor()),
                contentAlignment = Alignment.TopEnd
            ) {
                if (color.equalsColor(dotColor)) {
                    Checkmark()
                }
            }
        }
    }
}

@Composable
private fun DotSizes(dotSize: DotSize?, onSizeChange: (DotSize) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        DotSize.values().reversed().forEach { size ->
            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onSizeChange(size) }
                    .background(Color(0xFF505050)),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = 14.dp),
                    text = size.sizeName,
                    style = Typography.titleLarge.copy(color = LightGrey, textAlign = TextAlign.Center),
                    color = Color.LightGray
                )
                if (size == dotSize) {
                    Checkmark()
                }
            }
        }
    }
}

@Composable
private fun Checkmark() {
    Icon(
        modifier = Modifier
            .padding(4.dp)
            .size(12.dp),
        painter = painterResource(id = R.drawable.ic_check),
        tint = Color.White,
        contentDescription = "check_mark"
    )
}

@Composable
private fun StickedInfo(dotIsSticked: Boolean, onStickedChanged: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            style = Typography.titleLarge.copy(color = LightGrey),
            text = "Sticked"
        )
        Checkbox(
            colors = CheckboxDefaults.colors(
                checkedColor = Color.DarkGray,
                uncheckedColor = Color.Gray
            ),
            modifier = Modifier.padding(start = 4.dp),
            checked = dotIsSticked,
            onCheckedChange = { onStickedChanged(it) })
    }
}

@Composable
private fun ReminderInfo(dot: Dot) {
    val text = dot.reminder?.let { TimeUtils.formattedDate(it) }
        ?: "No reminder"
    Text(
        style = Typography.titleLarge.copy(color = LightGrey),
        text = text
    )
}

@Composable
private fun DotMessage(text: String, onTextChange: (String) -> Unit) {
    BasicTextField(
        modifier = Modifier
            .height(168.dp)
            .fillMaxWidth()
            .padding(8.dp),
        textStyle = Typography.titleLarge,
        cursorBrush = SolidColor(dotYellow.toColor()),
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.Sentences
        ),
        decorationBox = { innerTextField ->
            Row(modifier = Modifier.fillMaxWidth()) {
                if (text.isEmpty()) {
                    Text("Enter note", color = Color.Gray, fontSize = 12.sp)
                }
            }

            innerTextField()
        },
        value = text,
        onValueChange = onTextChange,
    )
}

@Preview
@Composable
fun DialogPreview() {
    DialogContent(Dot(
        1L, "", 0F, 0F, DotSize.MEDIUM, dotTeal, isSticked = true, createdDate = 1636109037074L,
        reminder = 1636109037074L + 51 * 60 * 1000
    ), { }, { }, { }, { })
}
