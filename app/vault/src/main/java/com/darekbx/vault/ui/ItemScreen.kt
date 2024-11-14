package com.darekbx.vault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ItemScreen(vaultViewModel: VaultViewModel = hiltViewModel(), id: Long) {
    val item by vaultViewModel.getItem(id).collectAsState(initial = null)

    item?.let { secretItem ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = secretItem.key, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                InputField(secretItem.account)
                Spacer(modifier = Modifier.height(8.dp))
                InputField(secretItem.password)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InputField(value: String) {
    TextField(
        modifier = Modifier
            .width(256.dp)
            .padding(4.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .background(Color.White),
        value = value,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            errorIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        readOnly = true,
        onValueChange = { },
        shape = RoundedCornerShape(8.dp),
        textStyle = TextStyle(fontSize = 18.sp, textAlign = TextAlign.Center),
        singleLine = true
    )
}