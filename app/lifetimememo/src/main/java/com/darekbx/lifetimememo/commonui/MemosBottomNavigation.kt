package com.darekbx.lifetimememo.commonui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.darekbx.lifetimememo.navigation.MemoDestination
import com.darekbx.lifetimememo.commonui.theme.Secondary
import java.util.*

private val TabHeight = 56.dp

@Composable
fun MemosBottomNavigation(
    allScreens: List<MemoDestination>,
    onTabSelected: (MemoDestination) -> Unit,
    currentScreen: MemoDestination
) {
    Surface(
        Modifier
            .height(TabHeight)
            .fillMaxWidth()
    ) {
        Row(Modifier
            .background(Secondary)
            .fillMaxSize(), horizontalArrangement = Arrangement.SpaceAround) {
            allScreens.forEach { screen ->
                MemoTab(
                    text = screen.label,
                    onSelected = { onTabSelected(screen) },
                    selected = currentScreen == screen
                )
            }
        }
    }
}


@Composable
private fun MemoTab(
    modifier: Modifier = Modifier,
    text: String,
    onSelected: () -> Unit,
    selected: Boolean
) {
    val labelStyle = TextStyle(
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        textDecoration = if (selected) TextDecoration.Underline else TextDecoration.None
    )
    Row(
        modifier = modifier
            .padding(16.dp)
            .height(TabHeight)
            .selectable(
                selected = selected,
                onClick = onSelected
            )
    ) {
        Text(text.uppercase(Locale.getDefault()), style = labelStyle)
    }
}