@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.lifetimememo.screens.category.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.expenses.ui.AddCategoryDialog
import com.darekbx.lifetimememo.commonui.ChipMark
import com.darekbx.lifetimememo.commonui.ProgressIndicator
import com.darekbx.lifetimememo.commonui.theme.*
import com.darekbx.lifetimememo.screens.category.CategoryUiState
import com.darekbx.lifetimememo.screens.category.model.Category
import com.darekbx.lifetimememo.screens.category.viewmodel.CategoryViewModel

@Composable
fun CategoriesScreen(categoryViewModel: CategoryViewModel = hiltViewModel()) {
    var addDialogVisible by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addDialogVisible = true },
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        CategoriesList(modifier = Modifier.padding(innerPadding), categoryViewModel)
    }

    if (addDialogVisible) {
        AddCategoryDialog(
            onDismiss = { addDialogVisible = false },
            onAdd = { name ->
                addDialogVisible = false
                categoryViewModel.add(name)
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoriesList(
    modifier: Modifier = Modifier,
    categoryViewModel: CategoryViewModel
) {
    val categories by categoryViewModel.categories.observeAsState()
    val uiState by categoryViewModel.uiState.observeAsState()

    val onLongClick: (Category) -> Unit = { category ->
        if (category.hasNoRelatedEntries()) {
            categoryViewModel.delete(category.uid)
        }
    }

    Box(modifier.fillMaxSize()) {
        categories?.let {
            LazyColumn {
                items(it) { category ->
                    CategoryItem(
                        modifier = Modifier.combinedClickable(
                            onClick = { /* Do nothing */ },
                            onLongClick = { onLongClick(category) }
                        ),
                        category = category
                    )
                }
            }
        }
        if (uiState is CategoryUiState.InProgress) {
            ProgressIndicator()
        }
    }
}

@Preview
@Composable
private fun CategoryItem(
    modifier: Modifier = Modifier,
    category: Category = Category("", "Name")
) {
    Card(
        modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp),
        colors = CardDefaults.cardColors(containerColor =  Secondary)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(Paddings.Default),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val colors =
                if (category.entriesCount == 0)
                    listOf(Green, DarkGreen.copy(alpha = 0.4f))
                else
                    listOf(Orange, DarkOrange.copy(alpha = 0.4f))
            Text(text = category.name)
            ChipMark(
                modifier = Modifier,
                text = "${category.entriesCount} entries",
                colors = colors
            )
        }
    }
}