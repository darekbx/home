package com.darekbx.timeline.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.timeline.model.Entry
import com.darekbx.timeline.ui.TimeUtils
import com.darekbx.timeline.ui.theme.LocalColors
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EntriesListView(homeViewModel: HomeViewModel = hiltViewModel()) {
    val entries by homeViewModel.entries.collectAsState(initial = emptyList())

    LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        items(entries) { entry ->
            RevealSwipe(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .padding(start = 8.dp, end = 8.dp),
                backgroundCardEndColor = LocalColors.current.red,
                onBackgroundEndClick = { homeViewModel.delete(entry)  },
                directions = setOf(RevealDirection.EndToStart),
                hiddenContentEnd = {
                    Icon(
                        modifier = Modifier.padding(horizontal = 25.dp),
                        imageVector = Icons.Outlined.Delete,
                        tint = Color.White,
                        contentDescription = null
                    )
                }
            ) {
                EntryRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    entry = entry
                )
            }
        }
    }
}

@Composable
fun EntryRow(modifier: Modifier = Modifier, entry: Entry) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier,
            text = buildAnnotatedString {
                append(entry.title)
                withStyle(
                    style = SpanStyle(
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    )
                ) {
                    append("  (")
                    append(TimeUtils.formattedDate(entry.timestamp))
                    append(")")
                }
            },
            style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        )
        if (entry.description.isNotBlank()) {
            Text(
                modifier = Modifier,
                text = entry.description,
                style = TextStyle(
                    color = Color.LightGray,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
            )
        }
    }
}