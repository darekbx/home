@file:OptIn(ExperimentalMaterial3Api::class)

package com.darekbx.hejto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darekbx.common.ui.NoInternetView
import com.darekbx.common.utils.ConnectionUtils
import com.darekbx.hejto.navigation.*
import com.darekbx.hejto.ui.HejtoTheme
import com.darekbx.hejto.ui.saved.viewmodel.SavedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HejtoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            HejtoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (ConnectionUtils.isInternetConnected(LocalContext.current)) {
                        Scaffold(
                            content = { innerPadding ->
                                HejtoNavHost(
                                    modifier = Modifier.padding(
                                        innerPadding
                                    ), navController = navController
                                )
                            },
                            bottomBar = { BottomMenu(navController) }
                        )
                    } else {
                        NoInternetView(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }

    @Composable
    private fun BottomMenu(
        navController: NavHostController,
        savedViewModel: SavedViewModel = hiltViewModel()
    ) {
        val savedCount by savedViewModel.savedSlugs.collectAsState(initial = listOf())
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuItem(
                modifier = Modifier.clickable { navController.navigateSingleTopTo(FavouriteTags.route) },
                label = "Tags",
                icon = painterResource(id = R.drawable.ic_label),
                selected = navBackStackEntry?.destination?.route == FavouriteTags.route
            )
            MenuItem(
                modifier = Modifier.clickable { navController.navigateSingleTopTo(Communities.route) },
                label = "Communities",
                icon = painterResource(id = R.drawable.ic_communities),
                selected = navBackStackEntry?.destination?.route == Communities.route
            )
            MenuItem(
                modifier = Modifier.clickable { navController.navigateSingleTopTo(Board.route) },
                label = "Board",
                icon = painterResource(id = R.drawable.ic_board),
                selected = navBackStackEntry?.destination?.route == Board.route
            )
            MenuItem(
                modifier = Modifier.clickable { navController.navigateSingleTopTo(Saved.route) },
                label = "  Saved  ",
                icon = painterResource(id = R.drawable.ic_save),
                selected = navBackStackEntry?.destination?.route == Saved.route,
                count = savedCount.count()
            )
            /*
            TODO settings are not needed right now
            MenuItem(
                modifier = Modifier
                    .clickable { navController.navigateSingleTopTo(Settings.route) }
                    .alpha(0.2F),
                label = "Settings",
                icon = painterResource(id = R.drawable.ic_settings),
                selected = navBackStackEntry?.destination?.route == Settings.route
            )*/
        }
    }
}

@Composable
private fun MenuItem(
    modifier: Modifier = Modifier,
    label: String,
    icon: Painter,
    selected: Boolean,
    count: Int = 0
) {
    Box(modifier = Modifier, contentAlignment = Alignment.TopEnd) {
        Column(
            modifier = modifier.alpha(if (selected) 0.4F else 1F),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = icon,
                contentDescription = label,
                tint = Color.White
            )
            Text(
                text = label,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.W200,
            )
        }
        if (count > 0) {
            Text(
                text = "$count",
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        CircleShape
                    ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.W700,
            )
        }
    }
}


@OptIn(ExperimentalTextApi::class)
@Preview(widthDp = 200, heightDp = 200)
@Composable
fun Bambilotto() {
    val textMeasure = rememberTextMeasurer()
    Box(modifier = Modifier.fillMaxSize(), Alignment.BottomCenter) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            drawCircle(color = Color.Black, radius = size.minDimension / 3.0f, style = Stroke(4.0f))
            translate(-12F, 4F) {
                // B
                scale(0.8F, 1F) {
                    drawText(
                        textMeasurer = textMeasure,
                        text = "B",
                        topLeft = Offset(264F, 72F),
                        style = TextStyle(fontSize = 104.sp, fontWeight = FontWeight.W100)
                    )
                }
                // A
                drawLine(
                    Color.Black,
                    strokeWidth = 8F,
                    start = Offset(220F, 372F),
                    end = Offset(291F, 174F)
                )
                drawLine(
                    Color.Black,
                    strokeWidth = 8F,
                    start = Offset(290F, 300F),
                    end = Offset(244F, 300F)
                )
                // L
                drawLine(
                    Color.Black,
                    strokeWidth = 8F,
                    start = Offset(200F, 176F),
                    end = Offset(200F, 372F)
                )
                drawLine(
                    Color.Black,
                    strokeWidth = 8F,
                    start = Offset(197F, 369F),
                    end = Offset(222F, 369F)
                )
                // M
                drawLine(
                    Color.Black,
                    strokeWidth = 8F,
                    start = Offset(201F, 178F),
                    end = Offset(255F, 270F)
                )
                //_
                drawLine(
                    Color.Black,
                    strokeWidth = 8F,
                    start = Offset(180F, 176F),
                    end = Offset(222F, 176F)
                )
                // .
                drawCircle(Color.Black, radius = 12F, center = Offset(291F, 144F))
            }
        }
    }
}