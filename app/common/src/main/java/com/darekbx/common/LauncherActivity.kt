package com.darekbx.common

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback

open class LauncherActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Finish launcher activity on back press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.v(TAG, "Finish launcher activity on back press")
                finish()
            }
        })
    }

    companion object {
        private const val TAG = "LauncherActivity"
    }
}