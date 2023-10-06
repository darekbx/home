package com.darekbx.infopigula.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.darekbx.common.ui.InformationDialog
import com.darekbx.infopigula.R
import com.darekbx.infopigula.model.User
import com.darekbx.infopigula.navigation.LoginDestination
import com.darekbx.infopigula.navigation.SettingsDestination
import com.darekbx.infopigula.ui.ProgressIndicator
import com.darekbx.infopigula.ui.theme.InfoPigulaTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun DrawerMenu(
    drawerMenuViewModel: DefaultDrawerMenuViewModel = hiltViewModel(),
    navController: NavController,
    closeDrawer: () -> Unit
) {
    DrawerMenuView(
        drawerMenuViewModel = drawerMenuViewModel,
        navigate = { route ->
            closeDrawer()
            navController.navigate(route)
        },
        close = { closeDrawer() }
    )
}

@Composable
private fun DrawerMenuView(
    drawerMenuViewModel: DrawerMenuViewModel,
    navigate: (route: String) -> Unit = { },
    close: () -> Unit = { },
) {
    val state by drawerMenuViewModel.uiState.collectAsState(initial = DrawerMenuUiState.Idle)
    var loggedUser by remember { mutableStateOf<User?>(null) }
    var isInProgress by remember { mutableStateOf(false) }
    var userFetchFailed by remember { mutableStateOf(false) }
    
    state.let { safeState ->
        when (safeState) {
            is DrawerMenuUiState.Done -> {
                isInProgress = false
                loggedUser = safeState.user
            }
            is DrawerMenuUiState.Failed -> {
                isInProgress = false
                userFetchFailed = true
            }
            DrawerMenuUiState.Idle -> isInProgress = false
            DrawerMenuUiState.InProgress -> isInProgress = true
            DrawerMenuUiState.NotLoggedIn -> isInProgress = false
            is DrawerMenuUiState.LoggedOut -> {
                loggedUser = null
                isInProgress = false
            }
        }
    }

    Box(
        Modifier
            .fillMaxHeight()
            .width(260.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 16.dp, top = 8.dp, end = 16.dp)
    ) {
        Column {
            TopBar(close)
            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "TODO: Display subscription plan details", color = Color.Red,
                fontSize = 11.sp)

            loggedUser?.let {
                ProfileView(it)
                Spacer(modifier = Modifier.height(32.dp))
            }

            if (loggedUser == null) {
                MenuItem(
                    Modifier.clickable { navigate(LoginDestination.route) },
                    "Login",
                    Icons.Default.AccountCircle
                )
            }
            MenuItem(
                Modifier.clickable { navigate(SettingsDestination.route) },
                "Settings",
                Icons.Default.Settings
            )

            if (loggedUser != null) {
                MenuItem(
                    Modifier.clickable { drawerMenuViewModel.logout() },
                    "Logout",
                    Icons.Outlined.ExitToApp
                )
            }
        }

        if (isInProgress) {
            ProgressIndicator()
        }

        if (userFetchFailed && state is DrawerMenuUiState.Failed) {
            val failedState = state as DrawerMenuUiState.Failed
            InformationDialog(message = "Failed to fetch user details! (${failedState.message})") {
                userFetchFailed = false
            }
        }
    }
}

@Composable
private fun ProfileView(user: User) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onBackground)
                    .padding(1.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                if (user.image != null) {

                    // TODO display user image

                } else {
                    Icon(
                        modifier = Modifier.size(42.dp),
                        imageVector = Icons.Default.Person,
                        contentDescription = "profile",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Text(
            modifier = Modifier
                .widthIn(max = 80.dp)
                .padding(start = 4.dp, end = 4.dp)
                .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                .padding(start = 4.dp, end = 4.dp),
            text = user.name,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
private fun MenuItem(modifier: Modifier, label: String, icon: ImageVector) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp, start = 8.dp, end = 8.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label.lowercase(),
            tint = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun TopBar(close: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.height(26.dp),
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "logo",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        IconButton(onClick = close) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "close",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview(name = "Light Theme Menu")
@Composable
fun MenuPreviewLight() {
    InfoPigulaTheme(isDarkTheme = false) {
        Box(Modifier.height(340.dp)) {
            DrawerMenuView(drawerMenuViewModel = object : DrawerMenuViewModel {
                override val uiState: Flow<DrawerMenuUiState> = emptyFlow()
                override fun logout() {}
            })
        }
    }
}

@Preview(name = "Dark Theme Menu")
@Composable
fun MenuPreviewDark() {
    InfoPigulaTheme(isDarkTheme = true) {
        Box(Modifier.height(340.dp)) {
            DrawerMenuView(drawerMenuViewModel = object : DrawerMenuViewModel {
                override val uiState: Flow<DrawerMenuUiState> = emptyFlow()
                override fun logout() {}
            })
        }
    }
}