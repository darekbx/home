package com.darekbx.words.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.theme.HomeTheme
import com.darekbx.storage.words.WordDto
import com.darekbx.words.R
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WordsScreen(wordsViewModel: WordsViewModel = hiltViewModel()) {
    val words by wordsViewModel.words().collectAsState(initial = emptyList())
    var addWordDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addWordDialog = true },
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = "add")
            }
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 8.dp)
            ) {
                items(words) { word ->
                    RevealSwipe(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(start = 8.dp, end = 8.dp),
                        backgroundCardEndColor = if (word.isArchived) Color(0xFFE75B52) else Color.DarkGray,
                        onBackgroundEndClick = { wordsViewModel.moveToArchived(word) },
                        shape = RoundedCornerShape(16.dp),
                        directions = setOf(RevealDirection.EndToStart),
                        hiddenContentEnd = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                painter = painterResource(id =
                                    if (word.isArchived) R.drawable.ic_delete
                                    else R.drawable.ic_archive
                                ),
                                tint = Color.LightGray,
                                contentDescription = null
                            )
                        },
                        backgroundStartActionLabel = null,
                        backgroundEndActionLabel = null
                    ) {
                        WordView(word = word) {
                            wordsViewModel.increaseCount(word)
                        }
                    }
                }
            }
        }
    )

    if (addWordDialog) {
        WordDialog(
            onDismiss = { addWordDialog = false },
            onWordAdded = { word, translation -> wordsViewModel.add(word, translation) }
        )
    }
}

@Composable
fun WordView(modifier: Modifier = Modifier, word: WordDto, translationShown: () -> Unit = { }) {
    var translationVisible by remember { mutableStateOf(false) }
    val translationBlur by animateDpAsState(
        targetValue = if (translationVisible) 0.dp else 10.dp,
        label = "translationBlurAnimation"
    )
    Box(
        modifier = modifier
            .background(Color.LightGray, RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .alpha(if (word.isArchived) 0.5F else 1F),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (!translationVisible) {
                        translationShown()
                    }
                    translationVisible = true
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .width(72.dp)
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    text = "${word.checkedCount}",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.DarkGray
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(3.dp)
                        .background(MaterialTheme.colorScheme.background)
                )
                Column(Modifier.padding(start = 12.dp, top = 4.dp)) {
                    Text(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        text = word.word,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.background
                    )
                    Text(
                        modifier = Modifier
                            .blur(translationBlur)
                            .padding(top = 6.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
                        text = word.translation,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }

            if (word.isArchived) {
                Box(
                    modifier = Modifier
                        .background(
                            Color.DarkGray,
                            RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                        )
                        .fillMaxHeight()
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(id = R.drawable.ic_archive),
                        tint = Color.LightGray,
                        contentDescription = "archive"
                    )
                }
            } else {
                /*
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 16.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    val formattedTimestamp = DateTimeUtils.formattedDate(word.timestamp)
                    Text(
                        modifier = Modifier
                            .rotate(90F)
                            .offset(y = -32.dp),
                        text = formattedTimestamp,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.DarkGray
                    )
                }
                */
            }
        }
    }
}

@Preview(showSystemUi = false)
@Composable
fun WordViewPreview() {
    HomeTheme(isDarkTheme = true) {
        Surface(
            Modifier
                .background(Color.Black)
                .padding(8.dp)) {
            WordView(word = WordDto(null, "Word", "Slowo", 1, false, System.currentTimeMillis()))
        }
    }
}

@Preview(showSystemUi = false)
@Composable
fun WordViewPreviewArchived() {
    HomeTheme(isDarkTheme = true) {
        Surface(
            Modifier
                .background(Color.Black)
                .padding(8.dp)) {
            WordView(word = WordDto(null, "Word", "Slowo", 5, true, System.currentTimeMillis()))
        }
    }
}
