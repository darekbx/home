package com.darekbx.lifetimememo.screens.settings.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.lifetimememo.R
import com.darekbx.lifetimememo.commonui.theme.Highlight
import com.darekbx.lifetimememo.commonui.theme.Paddings
import com.darekbx.lifetimememo.screens.settings.viewmodel.SettingsViewModel

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_2_XL
)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    openCategories: () -> Unit = { }
) {
    val context = LocalContext.current

    Column(Modifier.padding(Paddings.Default)) {
        SettingsRow(icon = painterResource(R.drawable.ic_category), title = "Categories") {
            openCategories()
        }
        SettingsRow(
            icon = painterResource(R.drawable.ic_backup),
            title = "Backup data"
        ) {
            settingsViewModel.makeBackup(context)
        }
        SettingsRow(
            icon = painterResource(R.drawable.ic_settings_restore),
            title = "Restore data from clipboard"
        ) {
            settingsViewModel.restoreFromBackup()
        }
    }
}

@Composable
private fun SettingsRow(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = Highlight
                ),
                onClick = onClick
            )
            .padding(Paddings.Default),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(Paddings.Small)
                .size(16.dp),
            painter = icon,
            contentDescription = title
        )
        Text(text = title)
    }
}