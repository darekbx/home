package com.darekbx.spreadsheet

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.darekbx.common.LauncherActivity
import com.darekbx.spreadsheet.navigation.SpreadsheetNavigation
import com.darekbx.spreadsheet.ui.theme.BasicSpreadsheetTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpreadSheetActivity : LauncherActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasicSpreadsheetTheme {
                SpreadsheetNavigation()
            }
        }
    }
}
