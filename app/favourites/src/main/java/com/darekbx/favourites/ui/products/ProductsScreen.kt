package com.darekbx.favourites.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.common.ui.ConfirmationDialog
import com.darekbx.storage.favourites.FavouriteItemDto
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe

@Preview
@Composable
fun ProductsScreen(productsViewModel: ProductsViewModel = hiltViewModel()) {
    var addCategoryDialogVisible by remember { mutableStateOf(false) }
    var deleteCategoryDialogVisible by remember { mutableStateOf(false) }
    var addItemDialogVisible by remember { mutableStateOf(false) }
    val isLoading by productsViewModel.isLoading
    val categories by productsViewModel.categories().collectAsState(initial = emptyList())
    var tabIndex by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(Modifier.fillMaxSize()) {
            ScrollableTabRow(selectedTabIndex = tabIndex) {
                categories.forEachIndexed { index, category ->
                    Tab(
                        text = { Text(text = category.name) },
                        selected = tabIndex == categories.indexOf(category),
                        onClick = { tabIndex = index },
                    )
                }

                // Add "plus tab"
                Tab(
                    selected = categories.isEmpty(),
                    onClick = { addCategoryDialogVisible = true },
                    icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") }
                )
            }

            when {
                categories.isEmpty() -> EmptyTab()
                else -> ProductsTab(
                    modifier = Modifier.fillMaxSize(),
                    categoryId = categories[tabIndex].id!!,
                    onAddClick = { addItemDialogVisible = true },
                    onDeleteCategoryClick = { deleteCategoryDialogVisible = true }
                )
            }
        }

        if (addCategoryDialogVisible) {
            CategoryDialog(
                onDismiss = { addCategoryDialogVisible = false },
                onCategoryAdded = { categoryName -> productsViewModel.addCategory(categoryName) }
            )
        }

        if (addItemDialogVisible) {
            ProductDialog(
                onDismiss = { addItemDialogVisible = false },
                onProductAdded = { name, comment, rating ->
                    productsViewModel.addItem(categories[tabIndex].id!!, name, comment, rating)
                }
            )
        }

        if (deleteCategoryDialogVisible) {
            ConfirmationDialog(
                message = "Delete category \"${categories[tabIndex].name}\" with all products?",
                confirmButtonText = "Yes",
                onDismiss = { deleteCategoryDialogVisible = false },
                onConfirm = { productsViewModel.deleteCategory(categories[tabIndex]) })
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.33F)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductsTab(
    modifier: Modifier,
    categoryId: Long,
    onAddClick: () -> Unit,
    onDeleteCategoryClick: () -> Unit,
    productsViewModel: ProductsViewModel = hiltViewModel()
) {
    val products by productsViewModel.products(categoryId).collectAsState(initial = emptyList())
    Column(modifier) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .weight(1F)
        ) {
            items(products) { product ->
                RevealSwipe(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .padding(start = 8.dp, end = 8.dp),
                    backgroundCardEndColor = Color(0xFFE75B52),
                    onBackgroundEndClick = { productsViewModel.deleteItem(product) },
                    directions = setOf(RevealDirection.EndToStart),
                    hiddenContentEnd = {
                        Icon(
                            modifier = Modifier.padding(horizontal = 25.dp),
                            imageVector = Icons.Outlined.Delete,
                            tint = Color.White,
                            contentDescription = null
                        )
                    },
                    backgroundStartActionLabel = null,
                    backgroundEndActionLabel = null
                ) {
                    ProductView(product = product)
                }
            }
        }
        Row(
            Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE75B52)),
                onClick = onDeleteCategoryClick
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "delete")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                onClick = onAddClick
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}

@Preview
@Composable
private fun ProductView(
    modifier: Modifier = Modifier,
    product: FavouriteItemDto = FavouriteItemDto(0L, 0L, "Name", 2F, "Long description", 0L)
) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
                Text(text = product.comment, fontSize = 11.sp, color = Color.DarkGray)
            }

            Text(
                text = "${product.rating.toInt()} of 5",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmptyTab() {
    val inlineContentMap = mapOf("plus_icon" to InlineTextContent(
        placeholder = Placeholder(16.sp, 16.sp, PlaceholderVerticalAlign.TextCenter)
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
    })
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "There are no categories.", color = Color.DarkGray)
            Text(text = buildAnnotatedString {
                append("Click tab \"")
                appendInlineContent(id = "plus_icon")
                append("\" button to add.")
            }, color = Color.DarkGray, inlineContent = inlineContentMap)
        }
    }
}
