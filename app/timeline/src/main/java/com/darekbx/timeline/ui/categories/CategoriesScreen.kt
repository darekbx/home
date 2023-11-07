package com.darekbx.timeline.ui.categories

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val usedColors by categoriesViewModel.usedColors().collectAsState(initial = emptyList())
    var dialogVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        categories
            ?.let { list ->
                if (list.isEmpty()) {
                    EmptyView(Modifier.align(Alignment.Center))
                } else {
                    CategoryList(modifier = Modifier.fillMaxWidth(), items = list)
                }
            }
            ?: run { LoadingView(Modifier.align(Alignment.Center)) }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = { dialogVisible = true }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "add")
        }
    }

    if (dialogVisible) {
        CategoryDialog(
            usedColors = usedColors,
            onSave = { name, color -> categoriesViewModel.add(name, color) },
            onDismiss = { dialogVisible = false }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryList(modifier: Modifier = Modifier, items: List<Category>) {
    LazyColumn(modifier = modifier.padding(8.dp)) {
        items(items) {
            CategoryRow(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                    .animateItemPlacement(), category = it)
        }
    }
}

@Composable
fun CategoryRow(modifier: Modifier, category: Category) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = category.name)
        Spacer(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(color = category.color))
                .border(0.5.dp, Color.White, RoundedCornerShape(16.dp))
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
fun LoadingView(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier)
}

@Preview
@Composable
fun CategoryRowPreview() {
    TimelineTheme {
        Surface {
            CategoryRow(
                modifier = Modifier.width(200.dp),
                category = Category(1L, "Companies", android.graphics.Color.MAGENTA)
            )
        }
    }
}
