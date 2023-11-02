package com.darekbx.timeline.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.timeline.model.Category
import com.darekbx.timeline.ui.theme.TimelineTheme

@Composable
fun CategoriesScreen(categoriesViewModel: CategoriesViewModel = hiltViewModel()) {
    val categories by categoriesViewModel.categories.collectAsState(initial = null)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        categories
            ?.let { list ->
                if (list.isEmpty()) {
                    EmptyView()
                } else {
                    CategoryList(modifier = Modifier.fillMaxWidth(), items = list)
                }
            }
            ?: run { LoadingView() }
    }
}

@Composable
fun CategoryList(modifier: Modifier = Modifier, items: List<Category>) {
    LazyColumn(modifier = modifier.padding(8.dp)) {
        items(items) {
            CategoryRow(category = it)
        }
    }
}

@Composable
fun CategoryRow(category: Category) {
    Row {
        Text(text = category.name)
        Spacer(modifier = Modifier
            .padding(4.dp)
            .size(24.dp)
            .clip(CircleShape)
            .background(Color(color = category.color))
        )
    }
}

@Preview
@Composable
fun EmptyView(modifier: Modifier = Modifier) {
    Text(text = "There are no categories")
}

@Preview
@Composable
fun LoadingView(modifier: Modifier = Modifier.size(48.dp)) {
    CircularProgressIndicator(modifier)
}

@Preview
@Composable§
fun CategoryRowPreview() {
    TimelineTheme {
        Surface {
            CategoryRow(category = Category(1L, "Companies", Color.Magenta.value))
        }
    }
}