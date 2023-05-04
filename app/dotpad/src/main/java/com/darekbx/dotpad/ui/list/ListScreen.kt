package com.darekbx.dotpad.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.darekbx.dotpad.R
import com.darekbx.dotpad.ui.CommonLoading
import com.darekbx.dotpad.ui.dots.Dot
import com.darekbx.dotpad.ui.dots.toColor
import com.darekbx.dotpad.ui.theme.LightGrey
import com.darekbx.dotpad.ui.theme.Typography
import com.darekbx.dotpad.utils.TimeUtils

@ExperimentalFoundationApi
@Composable
fun ListScreen(dots: State<List<Dot>?>) {
    Column(
        Modifier
            .fillMaxHeight()
            .padding(bottom = 58.dp)
    ) {
        dots.value?.let { dotsList ->
            DotList(dotsList.reversed())
        } ?:  CommonLoading()
    }
}

@Composable
private fun DotList(dots: List<Dot>) {
    if (dots.isEmpty()) {
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
            items(dots) { dot ->
                DotItem(dot) { /* Do nothing on click */ }
                Divider(color = Color.DarkGray)
            }
        }
    }
}


@Composable
fun DotItem(dot: Dot, onClick: () -> Unit) {
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
        if (dot.hasReminder()) {
            Text(
                text = stringResource(
                    id = R.string.reminder_format,
                    dot.reminder?.let { TimeUtils.formattedDate(it) } ?: ""),
                style = Typography.titleLarge.copy(color = LightGrey)
            )
        }
    }
}
