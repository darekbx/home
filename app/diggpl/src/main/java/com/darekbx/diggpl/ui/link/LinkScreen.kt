package com.darekbx.diggpl.ui.link

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.diggpl.R
import com.darekbx.diggpl.data.remote.*
import com.darekbx.diggpl.ui.*
import com.darekbx.diggpl.ui.comments.CommentsLazyList

@Composable
fun LinkScreen(
    linkId: Int,
    linkViewModel: LinkViewModel = hiltViewModel()
) {
    val link by linkViewModel.loadLink(linkId).collectAsState(initial = null)
    val releated by linkViewModel.loadLinkRelated(linkId).collectAsState(initial = null)

    CommentsLazyList(linkId = linkId, header = {
        LinkContentHeader(link, releated)
    })
}

@Composable
private fun LinkContentHeader(
    link: ResponseResult<DataWrapper<StreamItem>>?,
    releated: ResponseResult<DataWrapper<List<Related>>>?
) {
    Column {
        link?.let { result ->
            when (result) {
                is ResponseResult.Success -> LinkContent(result.data.data)
                is ResponseResult.Failure -> ErrorMessage(
                    result.error.message ?: "Unknown error"
                )
            }
        } ?: LoadingView()

        Spacer(modifier = Modifier.height(8.dp))

        releated?.let { result ->
            when (result) {
                is ResponseResult.Success -> RelatedView(result.data.data)
                is ResponseResult.Failure -> ErrorMessage(
                    result.error.message ?: "Unknown error"
                )
            }
        } ?: LoadingView()

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun LoadingView() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) { LoadingProgress() }
}

@Composable
private fun LinkContent(link: StreamItem) {
    Column(modifier = Modifier.padding(4.dp)) {
        val localUriHandler = LocalUriHandler.current
        Text(
            modifier = Modifier.clickable {
                link.source?.url?.let {
                    localUriHandler.openUri(it)
                }
            },
            text = link.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        MarkdownContent(link.description)
        Spacer(modifier = Modifier.height(8.dp))
        link.media.survey?.let {
            SurveyView(survey = it)
        }
        LinkImages(link)
        LinkSource(link)
    }
}

@Composable
private fun RelatedView(relatedItems: List<Related>) {
    val localUriHandler = LocalUriHandler.current
    relatedItems.forEach { item ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(64.dp)) {
                    item.media.photo?.let {
                        CommonImage(it, item.adult) {
                            localUriHandler.openUri(it.url)
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        modifier = Modifier,
                        text = item.title,
                        fontWeight = FontWeight.W600,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text(
                            text = "${item.author.username}: ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = item.source?.label ?: item.source?.url ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            Icon(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        item.source?.url?.let {
                            localUriHandler.openUri(it)
                        }
                    },
                painter = painterResource(id = R.drawable.ic_open),
                contentDescription = "open",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
