@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.lifetimememo.screens.search.ui

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.lifetimememo.commonui.ElementsList
import com.darekbx.lifetimememo.commonui.theme.Secondary
import com.darekbx.lifetimememo.screens.search.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    onMemoClick: (id: String?) -> Unit,
    onContainerClick: (id: String?) -> Unit
) {
    val filterState = remember { mutableStateOf("") }
    val elements by searchViewModel.searchInElements(filterState.value).observeAsState()

    Column {
        SerachBox { query ->
            filterState.value = query
        }
        ElementsList(
            elements ?: emptyList(),
            onContainerClick,
            onMemoClick
        )
    }
}

@Preview
@Composable
private fun SerachBox(onSearch: (String) -> Unit = { }) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Secondary, shape = RoundedCornerShape(16.dp))
            .padding(start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var value by remember { mutableStateOf("") }
        Icon(
            modifier = Modifier.padding(start = 6.dp),
            imageVector = Icons.Default.Search,
            contentDescription = "Search"
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth(1F)
                .onKeyEvent {
                    if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                        onSearch(value)
                    }
                    false
                },
            singleLine = true,
            value = value,
            onValueChange = { value = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(value) }),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Secondary,
                focusedIndicatorColor = Secondary,
                unfocusedIndicatorColor = Secondary
            ),
            label = { Text("Search") }
        )
    }
}