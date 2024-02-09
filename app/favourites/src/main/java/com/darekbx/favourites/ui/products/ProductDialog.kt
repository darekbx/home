package com.darekbx.favourites.ui.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.favourites.R

@Preview
@Composable
fun ProductDialog(
    onDismiss: () -> Unit = { },
    onProductAdded: (String, String, Float) -> Unit = { _, _, _ -> },
    maxRating: Int = 5
) {
    var name by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add new item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = "Name") })
                TextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text(text = "Comment") })

                Row {
                    (0..maxRating - 1).forEach { index ->
                        val icon =
                            if (index < rating) painterResource(id = R.drawable.ic_star)
                            else painterResource(id = R.drawable.ic_star_outline)
                        Icon(
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable { rating = index + 1 },
                            painter = icon,
                            contentDescription = "r_$index"
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onProductAdded(name, comment, rating.toFloat())
                onDismiss()
            }) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        }
    )
}
