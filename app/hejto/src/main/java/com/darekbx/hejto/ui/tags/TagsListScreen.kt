@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.darekbx.hejto.ui.tags

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.darekbx.hejto.R
import com.darekbx.hejto.data.remote.Tag
import com.darekbx.hejto.ui.HejtoTheme
import com.darekbx.hejto.ui.posts.ErrorIcon
import com.darekbx.hejto.ui.posts.LoadingProgress
import com.darekbx.hejto.ui.tags.viewmodel.TagsViewModel

class TagVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(buildAnnotatedString {
            append('#')
            append(text)
        }, object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset + 1
            }

            override fun transformedToOriginal(offset: Int): Int {
                return offset - 1
            }
        })
    }
}

@Composable
fun TagsListScreen(tagsViewModel: TagsViewModel = hiltViewModel()) {
    val favouriteTags by tagsViewModel.getFavouriteTags().collectAsState(initial = emptyList())
    val tags = tagsViewModel.tags.collectAsLazyPagingItems()
    var tagName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (tags.loadState.refresh) {
            is LoadState.Loading -> LoadingProgress()
            is LoadState.Error -> ErrorIcon()
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp, bottom = 4.dp)
                ) {
                    stickyHeader {
                        Row {
                            TextField(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .weight(1F),
                                value = tagName,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(onSend = {
                                    tagsViewModel.addRemoveFavouriteTag(tagName)
                                }),
                                visualTransformation = TagVisualTransformation(),
                                label = { Text("Enter tag name") },
                                onValueChange = { tagName = it }
                            )
                            Button(
                                modifier = Modifier
                                    .padding(
                                        top = 8.dp,
                                        end = 8.dp,
                                        bottom = 8.dp
                                    )
                                    .height(57.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(4.dp)
                                    ),
                                onClick = { tagsViewModel.addRemoveFavouriteTag(tagName) }) {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "add"
                                )
                            }
                        }
                    }
                    items(items = tags) { item ->
                        item?.let {
                            item.isFavourite = favouriteTags.any { it.name == item.name }
                            TagView(item, tagsViewModel::addRemoveFavouriteTag)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagView(tag: Tag, onFavouriteClick: (name: String) -> Unit = { }) {
    var isFavourite by remember { mutableStateOf(tag.isFavourite) }
    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "#${tag.name}",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .width(160.dp),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${tag.postsCount} entries",
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp, top = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${tag.followsCount}",
                modifier = Modifier
                    .padding(end = 4.dp, top = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                modifier = Modifier
                    .width(16.dp)
                    .padding(top = 4.dp),
                painter = painterResource(id = R.drawable.ic_views),
                contentDescription = "views",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Icon(
            modifier = Modifier
                .width(32.dp)
                .clickable {
                    isFavourite = !isFavourite
                    onFavouriteClick(tag.name)
                },
            imageVector =
            if (isFavourite) Icons.Default.Favorite
            else Icons.Outlined.FavoriteBorder,
            contentDescription = "views",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Preview
@Composable
private fun TagViewPreview() {
    HejtoTheme {
        TagView(tag = Tag("motoryzacja", 512, 21))
    }
}