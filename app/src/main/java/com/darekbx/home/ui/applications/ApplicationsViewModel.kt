package com.darekbx.home.ui.applications

import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import com.darekbx.home.ui.applications.model.AppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ApplicationsViewModel @Inject constructor(
    private val packageManager: PackageManager
    /* TODO position manager */
) : ViewModel() {

    fun applications(): List<AppInfo> {
        val packages = packageManager.queryIntentActivities(
            categoryLauncherIntent, PackageManager.MATCH_ALL)
        return packages.map { resolveInfo ->
            val packageName = resolveInfo.activityInfo.applicationInfo.packageName
            val label = resolveInfo.loadLabel(packageManager).toString()
            val icon = packageManager.getApplicationIcon(packageName)
            AppInfo(label, packageName, icon, 0 /* TODO */)
        }
    }

    private val categoryLauncherIntent: Intent by lazy {
        Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
    }
}