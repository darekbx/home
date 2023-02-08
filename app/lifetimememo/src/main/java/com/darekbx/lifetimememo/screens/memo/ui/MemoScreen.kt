@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.darekbx.lifetimememo.screens.memo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.lifetimememo.commonui.CancelIcon
import com.darekbx.lifetimememo.commonui.SaveIcon
import com.darekbx.lifetimememo.commonui.theme.Paddings
import com.darekbx.lifetimememo.screens.category.model.Category
import com.darekbx.lifetimememo.screens.category.viewmodel.CategoryViewModel
import com.darekbx.lifetimememo.screens.memos.viewmodel.MemosViewModel
import org.osmdroid.util.GeoPoint

/**
 * @param memoId Id of the memo which should be loaded into view
 * @param parentId Id of parent container to which memo should be added
 */
@Composable
fun MemoScreen(
    memosViewModel: MemosViewModel = hiltViewModel(),
    memoId: String? = null,
    parentId: String? = null,
    onClose: () -> Unit
) {
    val memoLocation = memosViewModel.getLocation(memoId).observeAsState()
    val memoState = remember { MemoState() }

    LaunchedEffect(memoId) {
        memosViewModel.getMemo(memoId)?.let { loadedMemo ->
            memoState.loadState(loadedMemo)
        }
    }

    val parentContainer = memosViewModel
        .getContainer(parentId ?: memoState.parentId)
        .observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Container") },
                actions = {
                    IconButton(onClick = { onClose() }) { CancelIcon() }
                    IconButton(onClick = {
                        memoState.titleValid = true
                        memoState.categoryValid = true
                        if (memoState.title.isBlank()) {
                            memoState.titleValid = false
                        } else if (memoState.categoryId.isEmpty()) {
                            memoState.categoryValid = false
                        } else {
                            memoId
                                ?.let { memoState.update(memoId, memosViewModel) }
                                ?: run { memoState.add(parentId, memosViewModel) }
                            onClose()
                        }
                    }) { SaveIcon() }
                }
            )
        },
        content = { padding ->
            var locationDialogVisible by remember { mutableStateOf(false) }
            var location by remember { mutableStateOf<GeoPoint?>(null) }
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    Modifier
                        .padding(padding)
                        .padding(top = 8.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(modifier = Modifier.padding(start = 16.dp)) {
                        Text(text = "Parent: ")
                        Text(
                            text = parentContainer.value?.title ?: "Root",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    with(memoState) {
                        TitleField(title, titleValid) { title = it }
                        CategorySelection(memoState = memoState)
                        SubtitleField(subtitle) { subtitle = it }
                        DescriptionField(description) { description = it }
                        LinkField(link) { link = it }
                        FlagsRow(memoState)
                        LocationRow(
                            memoState,
                            onAddLocation = { locationDialogVisible = true },
                            onOpenLocation = {
                                memoLocation.value?.let {
                                    location = GeoPoint(it.latitude, it.longitude)
                                    locationDialogVisible = true
                                }
                            }
                        )
                    }
                }

                if (locationDialogVisible) {
                    LocationView(location) { lat, lng ->
                        memoState.lat = lat
                        memoState.lng = lng
                        locationDialogVisible = false
                    }
                }
            }
        }
    )
}

@Composable
private fun LocationRow(
    memoState: MemoState,
    onAddLocation: () -> Unit = { },
    onOpenLocation: () -> Unit = { }
) {
    if (memoState.hasLocation) {
        Button(
            modifier = Modifier
                .inputPadding()
                .fillMaxWidth(),
            onClick = { onOpenLocation() }) {
            Text(text = "Show location")
        }
    } else {
        Button(
            modifier = Modifier
                .inputPadding()
                .fillMaxWidth(),
            onClick = { onAddLocation() }
        ) {
            val text =
                if (memoState.lat == null) "Add location"
                else "Location added"
            Text(text = text)
        }
    }
}

@Composable
private fun FlagsRow(memoState: MemoState) {
    Row(
        modifier = Modifier.padding(start = 4.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(checked = memoState.important, onCheckedChange = {
                memoState.important = it
                if (memoState.important) {
                    memoState.sticked = false
                }
            })
            Text(text = "Important")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(checked = memoState.sticked, onCheckedChange = {
                memoState.sticked = it
                if (memoState.sticked) {
                    memoState.important = false
                }
            })
            Text(text = "Sticked")
        }
    }
}

@Composable
private fun TitleField(
    value: String,
    isValid: Boolean,
    onChanged: (String) -> Unit
) {
    TextField(
        modifier = Modifier
            .inputPadding()
            .fillMaxWidth(),
        value = value,
        isError = !isValid,
        onValueChange = { onChanged(it) },
        singleLine = true,
        label = { Text("Title") }
    )
}

@Composable
private fun LinkField(
    value: String,
    onChanged: (String) -> Unit
) {
    TextField(
        modifier = Modifier
            .inputPadding()
            .fillMaxWidth(),
        value = value,
        onValueChange = { onChanged(it) },
        singleLine = true,
        label = { Text("Link (url)") }
    )
}

@Composable
private fun SubtitleField(value: String, onChanged: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .inputPadding()
            .fillMaxWidth()
            .height(100.dp),
        value = value,
        onValueChange = { onChanged(it) },
        singleLine = false,
        maxLines = 3,
        label = { Text("Short info") }
    )
}

@Composable
private fun DescriptionField(value: String, onChanged: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .inputPadding()
            .fillMaxWidth()
            .height(150.dp),
        value = value,
        onValueChange = { onChanged(it) },
        singleLine = false,
        maxLines = 6,
        label = { Text("Description") }
    )
}

private fun Modifier.inputPadding() =
    padding(top = Paddings.Default, start = Paddings.Big, end = Paddings.Big)

@Composable
fun CategorySelection(
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    memoState: MemoState
) {
    val categories by categoryViewModel.categories.observeAsState()
    val text = remember { mutableStateOf("") }
    val isOpen = remember { mutableStateOf(false) }

    if (categories != null) {
        memoState.categoryId.takeIf { it.isNotEmpty() }?.let { selectedCategoryId ->
            val activeCategory = categories?.firstOrNull { it.uid == selectedCategoryId }
            if (activeCategory != null) {
                text.value = activeCategory.name
                memoState.categoryId = activeCategory.uid
            }
        }
    }

    categories?.let { categoryList ->
        Box(Modifier.padding(start = Paddings.Big, end = Paddings.Big)) {
            Column {
                OutlinedTextField(
                    value = text.value,
                    isError = !memoState.categoryValid,
                    onValueChange = { text.value = it },
                    label = { Text(text = "Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                DropDownList(
                    requestToOpen = isOpen.value,
                    list = categoryList,
                    request = { isOpen.value = it },
                    selectedCategory = {
                        text.value = it.name
                        memoState.categoryId = it.uid
                    }
                )
            }
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .padding(10.dp)
                    .clickable(
                        onClick = { isOpen.value = true }
                    )
            )
        }
    }
}

@Composable
fun DropDownList(
    requestToOpen: Boolean = false,
    list: List<Category>,
    request: (Boolean) -> Unit,
    selectedCategory: (Category) -> Unit
) {
    DropdownMenu(
        modifier = Modifier.fillMaxWidth(),
        expanded = requestToOpen,
        onDismissRequest = { request(false) },
    ) {
        list.forEach {
            DropdownMenuItem(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    request(false)
                    selectedCategory(it)
                },
                text = { Text(it.name) }
            )
        }
    }
}