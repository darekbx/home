package com.darekbx.weather

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.cloudy.Cloudy

// Preview will render as a landscape
@Preview(widthDp = 720, heightDp = 540)
@Composable
fun BlurryLauncher(modifier: Modifier = Modifier) {
    val contentScale = 70
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier.fillMaxSize(contentScale / 100F),
            contentAlignment = Alignment.BottomCenter
        ) {
            var expanded by remember { mutableStateOf(false) }
            val imageAlpha by animateDpAsState(
                targetValue = if (expanded) 20.dp else 0.dp,
                animationSpec = tween(3000)
            )
            val bgAlpha by animateFloatAsState(
                targetValue = if (expanded) 0.5F else 0F,
                animationSpec = tween(3000)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(radius = imageAlpha)
                    .clickable { expanded = !expanded }
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.moss),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = "background"
                )
                //HotseatBackground()
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    NavigationBar()
                    AtAGlance()
                    Workspace()
                    QSB()
                    Navigation()
                }
                // Black out bg
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = bgAlpha)))
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                NavigationBar(modifier = Modifier.alpha(0F))
                AtAGlance(modifier = Modifier.alpha(0F))
                Workspace(modifier = Modifier.alpha(1F), itemVisible = 5)
                QSB()
                Navigation(modifier = Modifier.alpha(1F), itemVisible = 2)

            }
        }
    }
}

// Preview will render as a landscape
@Preview(widthDp = 720, heightDp = 360)
@Composable
fun Launcher(modifier: Modifier = Modifier) {
    val contentScale = 70
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier.fillMaxSize(contentScale / 100F),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.moss),
                contentScale = ContentScale.FillWidth,
                contentDescription = "background"
            )
            //HotseatBackground()
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                NavigationBar()
                AtAGlance()
                Workspace()
                QSB()
                Navigation()
            }
        }
    }
}

@Composable
private fun HotseatBackground(modifier: Modifier = Modifier, percentHeight: Float = 54F) {
    Box(
        modifier = modifier
            .padding(top = 200.dp)
            .fillMaxWidth()
            .fillMaxHeight(percentHeight / 100F)
            .background(Color.White.copy(alpha = 0.3F))
    )
}

@Preview(widthDp = 720, heightDp = 64)
@Composable
private fun AtAGlance(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier,
            text = "Thuesday, 20 Dec",
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
    }
}

@Preview(widthDp = 720, heightDp = 16)
@Composable
private fun NavigationBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .padding(start = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier,
            text = "18:35",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
        Icon(
            modifier = Modifier,
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = Color.White
        )
    }
}

@Preview(widthDp = 720, heightDp = 52)
@Composable
private fun QSB(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .padding(start = 16.dp, end = 16.dp)
            .background(Color.White, shape = RoundedCornerShape(32.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(top = 6.dp, bottom = 8.dp, start = 16.dp),
            text = "G",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 16.dp),
            imageVector = Icons.Default.Share,
            contentDescription = "G"
        )
    }
}

@Preview(widthDp = 720, heightDp = 32)
@Composable
private fun Navigation(modifier: Modifier = Modifier, itemVisible: Int = -1) {
    val color = Color.White
    val size = 24.dp
    val icons = listOf(Icons.Default.ArrowBack, Icons.Default.Home, Icons.Default.List)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icons.forEachIndexed { index, imageVector ->
            Icon(
                modifier = Modifier
                    .size(size)
                    .alpha(if (itemVisible == -1 || itemVisible - 1 == index) 1F else 0F),
                imageVector = imageVector,
                tint = color,
                contentDescription = "back"
            )
        }
    }
}

@Preview(widthDp = 720, heightDp = 320)
@Composable
private fun Workspace(modifier: Modifier = Modifier, itemVisible: Int = -1) {
    val items = (0..7).toList()
    LazyVerticalGrid(
        modifier = modifier.fillMaxWidth(),
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(items) { item ->
            AppIcon(
                modifier = Modifier
                    .padding(12.dp)
                    .alpha(if (itemVisible == -1 || itemVisible - 1 == item) 1F else 0F),
                label = "Label #${item + 1}"
            )
        }
    }
}

@Preview
@Composable
private fun AppIcon(modifier: Modifier = Modifier, label: String = "Label") {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            modifier = Modifier
                .size(48.dp)
                .background(color = Color.White, shape = CircleShape)
                .padding(8.dp), imageVector = Icons.Default.Call, contentDescription = ""
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = label, color = Color.White, fontSize = 14.sp)
    }
}

/**
 * Blur list elements content and highlight one item
 * + implementation "com.github.skydoves:cloudy:0.1.1"
 */
@Composable
private fun TutorialsBlurViewSample() {
    Box(modifier = Modifier.fillMaxSize()) {
        TutorialsView()

        // Display list on the bottom, to show that list will appear from the bottom
        ItemsList(
            modifier = Modifier
                .padding(top = 500.dp)
                .background(Color.White),
            items = (0..30).toList()
        )
    }
}

@Preview
@Composable
fun TutorialsView(modifier: Modifier = Modifier) {
    var parentSize by remember { mutableStateOf(IntSize.Zero) }
    val items = (0..30).toList()
    val blurRadiusSize = 15

    Box(modifier = modifier.onGloballyPositioned {
        parentSize = it.size
    }) {
        ItemsList(items = items)

        Cloudy(
            radius = blurRadiusSize,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75F))
        ) { }

        val indexVisible = 14

        ItemsList(items = items, itemVisible = indexVisible)
    }
}

@Composable
private fun ItemsList(modifier: Modifier = Modifier, items: List<Int>, itemVisible: Int = -1) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(96.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier
                    .alpha(if (itemVisible == -1 || itemVisible - 1 == item) 1F else 0F)
                    .padding(4.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
            ) {
                Text(
                    text = "#${item + 1}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}