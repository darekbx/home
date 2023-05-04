package com.darekbx.dotpad

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darekbx.dotpad.navigation.NavigationItem
import com.darekbx.dotpad.ui.calendar.CalendarScreen
import com.darekbx.dotpad.ui.history.HistoryListScreen
import com.darekbx.dotpad.ui.list.ListScreen
import com.darekbx.dotpad.ui.dots.DotsBoardScreen
import com.darekbx.dotpad.ui.dots.ShowDatePicker
import com.darekbx.dotpad.ui.dots.ShowTimePicker
import com.darekbx.dotpad.ui.statistics.StatisticsScreen
import com.darekbx.dotpad.viewmodel.DotsViewModel
import com.darekbx.dotpad.ui.theme.dotpadTheme
import com.darekbx.dotpad.utils.RequestPermission
import com.darekbx.dotpad.widget.CountWidget
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@ExperimentalPermissionsApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        var LAST_COUNT = 0
    }

    @Inject lateinit var dotsViewModel: DotsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RequestPermission(Manifest.permission.READ_CALENDAR) {
                DisplayContent()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        notifyDotsCount()
    }

    /**
     * Notify dots count to the KLauncher
     */
    private fun notifyDotsCount() {
        sendBroadcast(Intent().apply {
            action = "com.darekbx.dotpad.refresh"
            putExtra("dotsCount", dotsViewModel.activeDotsCount)
            component = ComponentName("com.klauncher", "com.klauncher.DotsReceiver")
        })

        LAST_COUNT = dotsViewModel.activeDotsCount
        val widgetManager = AppWidgetManager.getInstance(this)
        val widgetComponentName = ComponentName(this, CountWidget::class.java)
        val widgetIds = widgetManager.getAppWidgetIds(widgetComponentName)

        val widgetUpdateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
            component = widgetComponentName
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        }
        sendBroadcast(widgetUpdateIntent)
    }

    @Composable
    private fun DisplayContent() {
        val navController = rememberNavController()
        this.window.statusBarColor = android.graphics.Color.BLACK
        dotpadTheme {
            Scaffold(
                containerColor = Color.Black,
                bottomBar = { BottomAppBar(navController) },
                content = { padding -> Navigation(Modifier.padding(padding), navController) }
            )
        }
    }

    @Composable
    fun Navigation(modifier: Modifier, navController: NavHostController) {
        NavHost(modifier = modifier, navController = navController, startDestination = NavigationItem.Home.route) {
            composable(NavigationItem.Home.route) {
                DotsBoard()
            }
            composable(NavigationItem.History.route) {
                History()
            }
            composable(NavigationItem.Statistics.route) {
                Statistics()
            }
            composable(NavigationItem.Calendar.route) {
                Calendar()
            }
            composable(NavigationItem.List.route) {
                List()
            }
        }
    }

    @Composable
    private fun History() {
        val dots = dotsViewModel.archivedDots().observeAsState(null)
        HistoryListScreen(
            dots,
            onRestore = { dot -> dotsViewModel.restore(dot) },
            onPermanentlyDelete = { dot -> dotsViewModel.delete(dot) }
        )
    }

    @Composable
    private fun List() {
        val dots = dotsViewModel.activeDots().observeAsState(null)
        //Uncomment to fill data from legacy db
        /*LaunchedEffect(Unit) {
            dotsViewModel.fillDataFromLegacyDb()
        }*/
        ListScreen(dots)
    }

    @Composable
    private fun Statistics() {
        val count = dotsViewModel.countAll().observeAsState(0)
        val colors = dotsViewModel.colorStatistics().observeAsState(listOf())
        val sizes = dotsViewModel.sizeStatistics().observeAsState(listOf())
        StatisticsScreen(count, colors, sizes)
    }

    @Composable
    private fun Calendar() {
        val dots = dotsViewModel.activeDots().observeAsState(null)
        CalendarScreen(dots)
    }

    @Composable
    private fun DotsBoard() {
        val dots = dotsViewModel.activeDots().observeAsState(listOf())
        DotsBoardScreen(
            items = dots,
            selectedDot = dotsViewModel.selectedDot.value,
            onSave = dotsViewModel::saveItem,
            onResetTime = dotsViewModel::resetTime,
            onShowDatePicker = dotsViewModel::showDatePicker,
            onRemove = dotsViewModel::moveToArchive,
            onSelectDot = dotsViewModel::selectDot,
            onDiscardChanges = dotsViewModel::discardChanges,
            dotDialogState = dotsViewModel.dialogState.value
        )

        if (dotsViewModel.datePickerState.value) {
            ShowDatePicker(dateCallback = { y, m, d ->
                dotsViewModel.saveDate(y, m, d)
                dotsViewModel.showTimePicker()
            })
        }

        if (dotsViewModel.timePickerState.value) {
            ShowTimePicker(timeCallback = { h, m ->
                dotsViewModel.dismissPickers()
                dotsViewModel.saveTime(h, m)
            })
        }

        if (dotsViewModel.deleteReminderState.value) {
            DeleteReminderDialog()
        }
    }

    @Composable
    private fun DeleteReminderDialog() {
        AlertDialog(
            containerColor = Color.Black,
            onDismissRequest = { dotsViewModel.dismissDeleteReminderDialog() },
            confirmButton = {
                Button(onClick = {
                    dotsViewModel.removeReminder()
                    dotsViewModel.dismissDeleteReminderDialog()
                }) {
                    Text(text = stringResource(id = R.string.yes))

                }
            },
            dismissButton = {
                Button(onClick = { dotsViewModel.dismissDeleteReminderDialog() }) {
                    Text(text = stringResource(id = R.string.no))
                }
            },
            text = {
                Text(text = stringResource(id = R.string.delete_reminder))
            }
        )
    }

    @Composable
    private fun BottomAppBar(navController: NavController) {
        val items = listOf(
            NavigationItem.Home,
            NavigationItem.History,
            NavigationItem.Statistics,
            NavigationItem.Calendar,
            NavigationItem.List,
        )
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        Column {
            Separator()
                NavigationBar(containerColor = Color.Black) {
                    items.forEach { item ->
                        BottomNavigationItem(item, currentRoute, navController)
                    }
                }
        }
    }

    @Composable
    private fun RowScope.BottomNavigationItem(
        item: NavigationItem,
        currentRoute: String?,
        navController: NavController
    ) {
        NavigationBarItem(
            icon = { Icon(painterResource(id = item.iconResId), contentDescription = item.route) },
            label = { Text(text = stringResource(item.labelResId), fontSize = 11.sp) },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            ),
            selected = currentRoute == item.route,
            onClick = {
                navController.navigate(item.route) {
                    navController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route) {
                            saveState = true
                        }
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }

    @Composable
    private fun Separator() {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
        ) {
            drawLine(Color.DarkGray, Offset(0F, 0F), Offset(size.width, 0F), strokeWidth = 2F)
        }
    }
}
