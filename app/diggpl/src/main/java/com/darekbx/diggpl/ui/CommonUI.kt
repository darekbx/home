@file:OptIn(ExperimentalFoundationApi::class)

package com.darekbx.diggpl.ui

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Dimension
import com.darekbx.diggpl.R
import com.darekbx.diggpl.data.remote.*
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun StreamView(
    streamItem: StreamItem,
    openStreamItem: (StreamItem) -> Unit = { },
    onLongClick: (StreamItem) -> Unit = { }
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp)
    ) {
        when (streamItem.resource) {
            ResourceType.ENTRY.type -> {
                EntryView(
                    streamItem,
                    onClick = { openStreamItem(streamItem) },
                    onLongClick = { onLongClick(streamItem) }
                )
            }
            ResourceType.LINK.type -> {
                LinkView(
                    streamItem,
                    onClick = { openStreamItem(streamItem) },
                    onLongClick = { onLongClick(streamItem) })
            }
            ResourceType.ENTRY_COMMENT.type -> Text(
                text = streamItem.resource,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LinkView(
    streamItem: StreamItem,
    onClick: () -> Unit = { },
    onLongClick: () -> Unit = { }
) {
    Box(Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(4.dp)) {
            LinkTitle(streamItem.title, streamItem.source)
            Spacer(modifier = Modifier.height(8.dp))
            MarkdownContent(streamItem.description)
            Spacer(modifier = Modifier.height(8.dp))
            streamItem.media.survey?.let {
                SurveyView(survey = it)
            }
            LinkImages(streamItem)
            LinkSource(streamItem)
            Spacer(modifier = Modifier.height(8.dp))
            LinkFooter(streamItem, onClick, onLongClick)
        }
        if (streamItem.hot) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp), horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hot),
                    contentDescription = "hot",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun LinkTitle(title: String, source: Source?) {
    val localUriHandler = LocalUriHandler.current
    Text(
        modifier = Modifier.clickable {
            source?.url?.let {
                localUriHandler.openUri(it)
            }
        },
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
private fun LinkFooter(
    streamItem: StreamItem,
    onClick: () -> Unit = { },
    onLongClick: () -> Unit = { }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() },
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                fontWeight = FontWeight.W600,
                style = MaterialTheme.typography.labelMedium,
                text = "${streamItem.votes.summary()}",
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = streamItem.date,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_message),
                contentDescription = "Comments",
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${streamItem.comments.count}",
                fontWeight = FontWeight.W600,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun LinkImages(streamItem: StreamItem) {
    val localUriHandler = LocalUriHandler.current
    Box(modifier = Modifier.fillMaxWidth()) {
        streamItem.media.photo?.let {
            CommonImage(it, streamItem.adult) {
                localUriHandler.openUri(it.url)
            }
        }
        streamItem.media.embed
            ?.takeIf { it.thumbnail != null }
            ?.let {
                CommonImage(MediaPhoto("", it.thumbnail!!, ""), streamItem.adult) {
                    localUriHandler.openUri(it.url!!)
                }
            }
    }
}

@Composable
fun LinkSource(streamItem: StreamItem) {
    Row(modifier = Modifier.padding(4.dp)) {
        Text(
            text = "${streamItem.author.username}: ",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = streamItem.source?.label ?: streamItem.source?.url ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun EntryView(
    streamItem: StreamItem,
    onClick: () -> Unit = { },
    onLongClick: () -> Unit = { }
) {
    val localUriHandler = LocalUriHandler.current
    Box(Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // green, orange, burgundy
                AuthorView(streamItem.author)
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = streamItem.author.username,
                        color = streamItem.author.validColor(),
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = streamItem.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            MarkdownContent(streamItem.content)
            Spacer(modifier = Modifier.height(8.dp))
            streamItem.media.survey?.let {
                SurveyView(survey = it)
            }
            Spacer(modifier = Modifier.height(8.dp))
            streamItem.media.photo?.let {
                CommonImage(it, streamItem.adult) {
                    localUriHandler.openUri(it.url)
                }
            }
            streamItem.media.embed
                ?.takeIf { it.thumbnail != null }
                ?.let {
                    Box(contentAlignment = Alignment.BottomCenter) {
                        CommonImage(MediaPhoto("", it.thumbnail!!, ""), streamItem.adult) {
                            localUriHandler.openUri(it.url!!)
                        }
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.onBackground)
                                .padding(4.dp),
                            text = it.url!!,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            EntryFooter(streamItem, onClick, onLongClick)
        }
        if (streamItem.hot) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hot),
                    contentDescription = "hot",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun EntryFooter(
    streamItem: StreamItem,
    onClick: () -> Unit = { },
    onLongClick: () -> Unit = { }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() },
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_message),
                contentDescription = "Comments",
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${streamItem.comments.count}",
                fontWeight = FontWeight.W600,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(widthDp = 300, backgroundColor = 0L)
@Composable
private fun EntryViewPreview() {
    DiggTheme {
        EntryView(
            streamItem = StreamItem(
                10,
                "Title",
                "19.02.2022 15:32:12",
                "slug",
                "Description #lego",
                "Content",
                Source("Label", "http://google.pl"),
                Author("user name", "", null, null),
                Comments(10),
                Votes(10, 4),
                listOf("#lego", "#tag"),
                adult = false,
                hot = true,
                Media(
                    null, null, Survey(
                        question = "Question?",
                        count = 20,
                        answers = listOf(
                            SurveyAnswer("Answer 1", 2),
                            SurveyAnswer("Answer 2", 17),
                            SurveyAnswer("Answer 3", 1),
                        )
                    )
                ),
                "resource"
            )
        )
    }
}

@Preview(widthDp = 300, backgroundColor = 0L)
@Composable
private fun LinkViewPreview() {
    DiggTheme {
        LinkView(
            streamItem = StreamItem(
                10,
                "Title",
                "19.02.2022 15:32:12",
                "slug",
                "Description #lego",
                "Content",
                Source("Label", "http://google.pl"),
                Author("user name", "", null, null),
                Comments(10),
                Votes(421, 4),
                listOf("#lego", "#tag"),
                adult = false,
                hot = true,
                Media(null, null, null),
                "resource"
            )
        )
    }
}

@Composable
fun MarkdownContent(content: String) {
    MarkdownText(
        modifier = Modifier,
        markdown = content,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun CommonImage(mediaPhoto: MediaPhoto, isNsfw: Boolean, onClick: (() -> Unit)? = null) {
    val maxImageHeigth = 1920
    var errorWidthFraction by remember { mutableStateOf(1F) }
    var imageBlur by remember { mutableStateOf(10.dp) }
    var imageAlpha by remember { mutableStateOf(0.1F) }
    val localUriHandler = LocalUriHandler.current
    val image = mediaPhoto.url
    var modifier = Modifier
        .padding(top = 8.dp, bottom = 8.dp)
        .clip(RoundedCornerShape(8.dp))
        .fillMaxWidth(errorWidthFraction)
        .padding(8.dp)
        .clickable {
            if (isNsfw && (imageBlur > 0.dp || imageAlpha < 0.5F)) {
                imageBlur = 0.dp
                imageAlpha = 1F
            } else {
                if (onClick == null) {
                    localUriHandler.openUri(image)
                } else {
                    onClick()
                }
            }
        }

    if (isNsfw) {
        modifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            modifier.then(Modifier.blur(imageBlur))
        } else {
            modifier.then(Modifier.alpha(imageAlpha))
        }
    }

    Box(
        modifier = Modifier.defaultMinSize(100.dp, 100.dp),
        contentAlignment = Alignment.Center
    ) {
        var isLoading by remember { mutableStateOf(true) }
        Image(
            contentScale = ContentScale.FillWidth,
            modifier = modifier,
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .size(Dimension.Undefined, Dimension(maxImageHeigth))
                    .build(),
                onLoading = { isLoading = true },
                onSuccess = { isLoading = false },
                onError = { errorWidthFraction = 0.1F },
                error = painterResource(id = R.drawable.ic_error)
            ),
            contentDescription = "image"
        )
        if (isLoading) {
            LoadingProgress()
        }
    }
}

@Composable
fun AuthorView(author: Author) {
    Column(modifier = Modifier.size(34.dp)) {
        val color = when (author.gender) {
            "m" -> Color(83, 129, 171)
            "f" -> Color(177, 80, 163)
            else -> Color.White
        }
        if (author.avatar.isBlank()) {
            Image(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RectangleShape),
                painter = painterResource(R.drawable.avatar_default),
                contentDescription = "avatar"
            )
        } else {
            Image(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RectangleShape),
                painter = rememberAsyncImagePainter(
                    author.avatar,
                    contentScale = ContentScale.Crop
                ),
                contentDescription = "avatar"
            )
        }
        Spacer(
            modifier = Modifier
                .size(32.dp, 2.dp)
                .background(color)
        )
    }
}

@Composable
fun SurveyView(survey: Survey) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = survey.question,
            fontWeight = FontWeight.W600,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .height(1.dp)
                .background(Color.White)
        )
        survey.answers.forEach {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                val percent = (it.count.toFloat() / survey.count) * 100F
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier,
                        text = it.text,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        modifier = Modifier,
                        text = "%.1f%% (${it.count})".format(percent),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(percent / 100F)
                        .background(Color.White.copy(alpha = 0.1F))
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(1.dp)
                .background(Color.White)
        )
        Text(
            modifier = Modifier.padding(4.dp),
            text = "Answers: ${survey.count}",
            fontWeight = FontWeight.W600,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )

    }
}

@Composable
fun ErrorIcon() {
    Icon(
        painterResource(id = R.drawable.ic_error),
        contentDescription = "error",
        tint = Color.Red,
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer, CircleShape
            )
            .padding(8.dp)
    )
}

@Composable
fun LoadingProgress() {
    CircularProgressIndicator(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer, CircleShape
            )
            .padding(8.dp)
    )
}

@Composable
fun ErrorMessage(error: String) {
    Text(
        text = error,
        color = Color.Red,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp)
            )
            .padding(8.dp)
    )
}

fun showAddedToast(localContext: Context) {
    Toast.makeText(localContext, "Added", Toast.LENGTH_SHORT).show()
}

