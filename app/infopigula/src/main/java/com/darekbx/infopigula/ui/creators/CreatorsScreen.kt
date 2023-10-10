package com.darekbx.infopigula.ui.creators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.infopigula.model.Creator
import com.darekbx.infopigula.ui.home.LoadingProgress
import com.darekbx.infopigula.ui.theme.InfoPigulaTheme
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun CreatorsScreen(
    creatorsViewModel: CreatorsViewModel = hiltViewModel()
) {
    val uiState by creatorsViewModel.uiState.collectAsState(initial = CreatorsUiState.Idle)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        uiState.let { state ->
            when (state) {
                is CreatorsUiState.Done -> CreatorsList(state.creators)
                is CreatorsUiState.Failed -> ErrorMessage(state.message)
                CreatorsUiState.InProgress -> LoadingProgress()
                CreatorsUiState.Idle -> {}
            }
        }
    }
}

@Composable
fun CreatorsList(creators: List<Creator>) {
    LazyColumn(modifier = Modifier.padding(8.dp)) {
        items(creators) { creator ->
            CreatorItem(creator = creator)
        }
    }
}

@Composable
fun CreatorItem(creator: Creator) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1F)) {
            MarkdownText(
                modifier = Modifier,
                markdown = creator.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            MarkdownText(
                modifier = Modifier,
                markdown = creator.description,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Row {
            Spacer(modifier = Modifier.width(8.dp))
            val icon =
                if (creator.flagged) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = icon,
                contentDescription = "favorite"
            )
        }
    }
}

@Composable
fun ErrorMessage(message: String = "Unknown error") {
    Text(
        modifier = Modifier,
        text = message,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Preview
@Composable
fun ErrorMessagePreview() {
    InfoPigulaTheme(isDarkTheme = false) {
        Surface {
            ErrorMessage()
        }
    }
}

@Preview
@Composable
fun CreatorItemPreview() {
    InfoPigulaTheme(isDarkTheme = false) {
        Surface {
            CreatorItem(
                Creator(
                    "Strategy \u0026amp; Future",
                    "\u003Cp\u003EThink tank dr Jacka Bartosiaka. dr Jacka Bartosiaka. . Geopolityka, a\u00a0sprawa polska",
                    false,
                    ""
                )
            )
        }
    }
}