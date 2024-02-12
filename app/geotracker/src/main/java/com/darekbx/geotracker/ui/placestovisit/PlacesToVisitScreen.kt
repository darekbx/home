package com.darekbx.geotracker.ui.placestovisit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.geotracker.navigation.BackPressHandler
import com.darekbx.geotracker.repository.model.PlaceToVisit
import com.darekbx.geotracker.ui.LoadingProgress
import com.darekbx.geotracker.ui.theme.GeoTrackerTheme
import com.darekbx.geotracker.ui.theme.LocalColors
import com.darekbx.geotracker.ui.theme.LocalStyles
import com.darekbx.geotracker.ui.trip.LabelDialog
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import org.osmdroid.util.GeoPoint

@Composable
fun PlacesToVisitScreen(
    placesToVisitUiState: PlacesToVisitState = rememberPlacesToVisitViewState(),
    navigateUp: () -> Unit
) {
    val state = placesToVisitUiState.state
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }

    var showAddLocation by remember { mutableStateOf(false) }
    var showAddLabelDialog by remember { mutableStateOf(false) }
    var addLocationPoint by remember { mutableStateOf<GeoPoint?>(null) }

    BackPressHandler {
        if (selectedLocation != null) {
            selectedLocation = null
        } else {
            navigateUp()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is PlacesToVisitUiState.Done -> PlacesList(
                places = state.places,
                onItemDeleteClick = { placesToVisitUiState.delete(it) },
                onItemClick = { selectedLocation = it.location() }
            )

            PlacesToVisitUiState.Idle -> {}
            PlacesToVisitUiState.InProgress -> LoadingProgress()
        }
        ActionButtons { showAddLocation = true }
    }

    if (selectedLocation != null) {
        LocationView(selectedLocation) { _, _ -> }
    }

    if (showAddLocation) {
        LocationView(null) { lat, lng ->
            addLocationPoint = GeoPoint(lat, lng)
            showAddLocation = false
            showAddLabelDialog = true
        }
    }

    if (showAddLabelDialog) {
        LabelDialog(
            label = "",
            title = "Add label to new location",
            onSave = { label ->
                placesToVisitUiState.add(label, addLocationPoint)
                showAddLabelDialog = false
            },
            onDismiss = { showAddLabelDialog = false }
        )
    }
}

@Composable
private fun ActionButtons(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            modifier = Modifier.padding(bottom = 32.dp, end = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                shape = RoundedCornerShape(50),
                onClick = { onAddClick() }
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "add",
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlacesList(
    places: List<PlaceToVisit>,
    onItemDeleteClick: (PlaceToVisit) -> Unit = { },
    onItemClick: (PlaceToVisit) -> Unit = { },
) {
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        items(places) { place ->
            RevealSwipe(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .padding(start = 8.dp, end = 8.dp),
                backgroundCardEndColor = LocalColors.current.red,
                onBackgroundEndClick = { onItemDeleteClick(place) },
                directions = setOf(RevealDirection.EndToStart),
                hiddenContentEnd = {
                    Icon(
                        modifier = Modifier.padding(horizontal = 25.dp),
                        imageVector = Icons.Outlined.Delete,
                        tint = Color.White,
                        contentDescription = null
                    )
                }
            ) {
                PlaceListItem(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                        .clickable { onItemClick(place) },
                    placeToVisit = place
                )
            }
        }
    }
}

@Composable
fun PlaceListItem(modifier: Modifier = Modifier, placeToVisit: PlaceToVisit) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                modifier = Modifier,
                text = placeToVisit.label,
                style = LocalStyles.current.title,
                fontSize = 16.sp,
            )
            Text(
                modifier = Modifier.padding(bottom = 0.dp),
                text = placeToVisit.formattedTimestamp(),
                style = LocalStyles.current.grayLabel,
                fontSize = 14.sp
            )
        }
    }
}

@Preview
@Composable
fun PlaceListItemPreview() {
    GeoTrackerTheme {
        PlaceListItem(
            placeToVisit = PlaceToVisit(
                1L,
                "Label",
                52.0,
                21.0,
                System.currentTimeMillis()
            )
        )
    }
}