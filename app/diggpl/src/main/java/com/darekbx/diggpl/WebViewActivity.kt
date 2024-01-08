package com.darekbx.diggpl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.darekbx.common.LauncherActivity
import com.darekbx.diggpl.ui.DiggTheme
import com.darekbx.diggpl.ui.internalweb.InternalWebView

class WebViewActivity : LauncherActivity() {

    companion object {
        val URL_PARAM = "url_param"

        fun openImage(context: Context, url: String) {
            context.startActivity(Intent(context, WebViewActivity::class.java).apply {
                putExtra(URL_PARAM,url)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiggTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    intent.getStringExtra(URL_PARAM)?.let {
                        InternalWebView(url = it, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Close image preview, when not in focus
        finish()
    }
}