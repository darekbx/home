package com.darekbx.hejto.ui.posts

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.darekbx.hejto.R
import com.darekbx.hejto.data.remote.*
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun PostContent(post: PostDetails) {
    MarkdownText(
        modifier = Modifier.padding(8.dp),
        markdown = post.content,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun CommonImage(remoteImage: RemoteImage, isNsfw: Boolean) {
    var errorWidthFraction by remember { mutableStateOf(1F) }
    var imageBlur by remember { mutableStateOf(100.dp) }
    var imageAlpha by remember { mutableStateOf(0.025F) }
    val localUriHandler = LocalUriHandler.current
    val image = remoteImage.urls?.values?.last()
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
                image?.let {
                    localUriHandler.openUri(it)
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

    Image(
        contentScale = ContentScale.FillWidth,
        modifier = modifier,
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(image)
                .size(Size.ORIGINAL)
                .build(),
            onError = { errorWidthFraction = 0.1F },
            error = painterResource(id = R.drawable.ic_error)
        ),
        contentDescription = "image"
    )
}

@Composable
fun PostHeader(post: PostDetails) {
    val ago by remember {
        derivedStateOf { post.dateAgo() }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AuthorAvatar(post.author)
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuthorName(post.author)
                    AuthorRank(post.author)
                }
                CommunityInfo(post, ago)
            }
        }
        PostLikesInfo(post.likesCount)
    }
}

@Composable
fun PostLikesInfo(likesCount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$likesCount",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_bolt),
            contentDescription = "likes",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun AuthorAvatar(author: Author) {
    val avatarSize = 32.dp
    if (author.avatar != null) {
        val image = author.avatar.urls?.values?.first()
        Image(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .border(1.dp, Color.White, CircleShape),
            painter = rememberAsyncImagePainter(image),
            contentDescription = author.userName
        )
    } else {
        // Placeholder
        Spacer(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .border(1.dp, Color.White, CircleShape)
                .background(MaterialTheme.colorScheme.onSurface),
        )
    }
}

@Composable
fun AuthorName(author: Author) {
    Text(
        text = author.userName,
        modifier = Modifier
            .padding(start = 8.dp)
            .widthIn(0.dp, 180.dp),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun AuthorRank(author: Author) {
    Text(
        text = author.rank,
        modifier = Modifier
            .padding(start = 8.dp)
            .background(
                Color(android.graphics.Color.parseColor(author.rankColor)),
                RoundedCornerShape(4.dp)
            )
            .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 2.dp),
        style = MaterialTheme.typography.labelSmall,
        color = Color.White
    )
}

@Composable
fun CommunityInfo(post: PostDetails, ago: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.W200)) {
                append("in")
            }
            append(" ${post.community.name} ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.W200)) {
                append(ago)
            }
        },
        modifier = Modifier.padding(start = 8.dp),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
fun ErrorIcon() {
    Icon(
        painterResource(id = R.drawable.ic_error),
        contentDescription = "error",
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

object MockData {
    val POST = PostDetails(
        "article",
        "Zarobki inżynierów",
        "zarobki-inzynierow",
        "Na FB istnieje spora grupa zrzeszająca inżynierów budownictwa. Co roku wypuszczana jest tam anonimowa ankieta odnośnie zarobków. Co prawda ankieta ruszyła jakiś czas temu, ale może ktoś jeszcze zechce wypełnić. Daje ona pogląd przy rozmowach z pracodawcą czego można oczekiwać. \n\n [#budownictwo](/tag/budownictwo)",
        false,
        listOf(
            RemoteImage(urls = mapOf("500x500" to "https://hejto-media.s3.e…35a112e5faaed57114a2.jpg"))
        ),
        tags = listOf(Tag("pieniadze"), Tag("budownictwo")),
        author = Author(
            "GregSummer LongAvatar name", "Kompan", "#7c5292", RemoteImage(
                mapOf(
                    "100x100" to "https://hejto-media.s3.eu-central-1.amazonaws.com/uploads/users/images/backgrounds/400x300/85b28c95ae9af101ee12d8e0dcc856a3.jpg",
                )
            )
        ),
        nsfw = false,
        controversial = false,
        likesCount = 7,
        commentsCount = 3,
        createdAt = "2023-01-20T20:21:18+01:00",
        community = CommunityCategory("Wiadomosci", "wiadomosci", 21),
        link = "https://streamable.com/gqsn7x"
    )
    val COMMENT = PostComment(
        content = "Na FB istnieje spora grupa zrzeszająca inżynierów budownictwa.",
        author = Author(
            "GregSummer LongAvatar name", "Kompan", "#7c5292", RemoteImage(
                mapOf(
                    "100x100" to "https://hejto-media.s3.eu-central-1.amazonaws.com/uploads/users/images/backgrounds/400x300/85b28c95ae9af101ee12d8e0dcc856a3.jpg",
                )
            )
        ),
        images = listOf(
            RemoteImage(urls = mapOf("500x500" to "https://hejto-media.s3.e…35a112e5faaed57114a2.jpg"))
        ),
        likesCount = 7,
        reportsCount = 2,
        createdAt = "2023-01-20T20:21:18+01:00"
    )
}