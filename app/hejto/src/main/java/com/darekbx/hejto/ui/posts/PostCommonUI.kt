package com.darekbx.hejto.ui.posts

import android.app.Activity
import android.os.Build
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
import androidx.compose.ui.draw.scale
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
import coil.decode.GifDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.darekbx.hejto.R
import com.darekbx.hejto.WebViewActivity
import com.darekbx.hejto.data.remote.*
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun PostContent(content: String) {
    MarkdownText(
        modifier = Modifier.padding(8.dp),
        markdown = content,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun ContentLinkView(contentLink: ContentLink, isNsfw: Boolean) {
    Box(modifier = Modifier) {
        contentLink.images.firstOrNull()?.let { image ->
            val remoteImage = RemoteImage(mapOf("url" to image.url))
            val context = LocalContext.current
            CommonImage(remoteImage, isNsfw) {
                WebViewActivity.openImage(context, contentLink.url)
            }
        }
        Box(modifier = Modifier, contentAlignment = Alignment.Center) {
            Icon(
                modifier = Modifier.scale(1.05F),
                painter = painterResource(id = R.drawable.ic_movie),
                contentDescription = "play",
                tint = Color.Black
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_movie),
                contentDescription = "play",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CommonImage(remoteImage: RemoteImage, isNsfw: Boolean, onClick: (() -> Unit)? = null) {

    // Override
    val isNsfw = false

    var errorWidthFraction by remember { mutableStateOf(1F) }
    var imageBlur by remember { mutableStateOf(100.dp) }
    var imageAlpha by remember { mutableStateOf(0.1F) }
    val context = LocalContext.current
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
                    if (onClick == null) {
                        WebViewActivity.openImage(context, it)
                    } else {
                        onClick()
                    }
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
                    .decoderFactory(GifDecoder.Factory())
                    .size(Size.ORIGINAL)
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

@ExperimentalFoundationApi
@Composable
fun PostHeader(post: PostDetails, onLongClick: (String) -> Unit) {
    val ago by remember {
        derivedStateOf { post.dateAgo() }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .combinedClickable(
                onClick = { },
                onLongClick = { onLongClick(post.slug) },
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AuthorAvatar(post.author.avatar)
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AuthorName(post.author.userName)
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
fun AuthorAvatar(remoteImage: RemoteImage?) {
    val avatarSize = 32.dp
    if (remoteImage != null) {
        val image = remoteImage.urls?.values?.first()
        Image(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .border(1.dp, Color.White, CircleShape),
            painter = rememberAsyncImagePainter(image),
            contentDescription = "avatar"
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
fun AuthorName(name: String) {
    Text(
        text = name,
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
                Color(author.color),
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
        listOf(
            ContentLink("", "", emptyList())
        ),
        tags = listOf(Tag("pieniadze", 33, 21), Tag("budownictwo", 1, 22)),
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
        community = Community("Wiadomosci", "wiadomosci", "", null, 1),
        uuid = "uuid",
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
        listOf(
            ContentLink("", "", emptyList())
        ),
        likesCount = 7,
        reportsCount = 2,
        createdAt = "2023-01-20T20:21:18+01:00"
    )
}