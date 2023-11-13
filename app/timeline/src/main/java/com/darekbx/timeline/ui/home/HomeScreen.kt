package com.darekbx.timeline.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.timeline.model.Category
import com.darekbx.timeline.ui.theme.CategoryColors

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onCategoriesClick: () -> Unit = { },
    onEntriesListClick: () -> Unit = { },
) {
    var addDialogVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            var selectedCategory by remember { mutableLongStateOf(-1L) }
            EntriesView(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth(),
                selectedCategory = selectedCategory
            )
            Divider(Modifier.fillMaxWidth())
            CategoriesFlowRow(modifier = Modifier) { selectedCategoryId ->
                selectedCategory = selectedCategoryId
            }
        }

        Column(
            modifier = Modifier
                .padding(end = 32.dp, bottom = 152.dp)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloatingActionButton(onClick = onCategoriesClick) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "categories")
            }
            FloatingActionButton(onClick = onEntriesListClick) {
                Icon(imageVector = Icons.Default.List, contentDescription = "list")
            }
            FloatingActionButton(onClick = { addDialogVisible = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add")
            }
        }
    }

    if (addDialogVisible) {
        EntryDialog(
            onSave = { categoryId, title, description, timestamp ->
                homeViewModel.add(categoryId, title, description, timestamp)
                addDialogVisible = false
            },
            onDismiss = { addDialogVisible = false }
        )
    }
}

@Composable
private fun EntriesView(
    modifier: Modifier = Modifier,
    selectedCategory: Long = -1L,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val entries by homeViewModel.entries.collectAsState(initial = emptyList())
    val filteredEntries = entries.filter {
        if (selectedCategory == -1L) true
        else it.categoryId == selectedCategory
    }
    if (filteredEntries.isNotEmpty()) {
        TimelineView(modifier = modifier, entries = filteredEntries)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoriesFlowRow(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    onCategorySelect: (Long) -> Unit = { }
) {
    val categories by homeViewModel.categories.collectAsState(initial = emptyList())
    var selectedCategory by remember { mutableLongStateOf(-1L) }
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        CategoryChip(
            Modifier.clickable {
                selectedCategory = -1L
                onCategorySelect(-1L)
            },
            Category(-1L, "All", android.graphics.Color.LTGRAY),
            selectedCategory == -1L
        )
        categories.forEach { category ->
            CategoryChip(
                Modifier.clickable {
                    selectedCategory = category.id
                    onCategorySelect(category.id)
                },
                category,
                selectedCategory == category.id
            )
        }
    }
}

@Preview
@Composable
fun CategoryChip(
    modifier: Modifier = Modifier,
    category: Category = Category(1L, "Work", CategoryColors[0]),
    isSelected: Boolean = false
) {
    Row(
        modifier = modifier
            .padding(4.dp)
            .border(
                color = Color(category.color),
                shape = RoundedCornerShape(16.dp),
                width = 2.dp
            )
            .background(
                color = Color(category.color).copy(alpha = 0.5F),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            modifier = Modifier
                .size(16.dp)
                .alpha(0F),
            imageVector = Icons.Default.Check,
            contentDescription = "checked"
        )
        Text(
            modifier = Modifier
                .padding(end = 4.dp, start = 4.dp),
            text = category.name
        )
        Icon(
            modifier = Modifier
                .size(16.dp)
                .alpha(if (isSelected) 1F else 0F),
            imageVector = Icons.Default.Check,
            contentDescription = "checked"
        )
    }
}
