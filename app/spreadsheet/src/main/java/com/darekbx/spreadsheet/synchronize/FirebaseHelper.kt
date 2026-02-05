package com.darekbx.spreadsheet.synchronize

import android.content.Context
import com.darekbx.spreadsheet.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseHelper @Inject constructor(private val context: Context) {

    val spreadsheetApp: FirebaseApp by lazy {
        val options = FirebaseOptions.Builder()
            .setApplicationId(BuildConfig.CLOUD_APPLICATION_ID)
            .setProjectId(BuildConfig.CLOUD_PROJECT_ID)
            .setStorageBucket(BuildConfig.CLOUD_STORAGE_BUCKET)
            .setApiKey(BuildConfig.CLOUD_API_KEY)
            .setGcmSenderId(BuildConfig.CLOUD_GCM_SENDER_ID)
            .build()
        FirebaseApp.initializeApp(context, options, "spreadsheet_app")
    }

    suspend fun checkAndAuthorize() {
        val email = BuildConfig.CLOUD_EMAIL
        val password = BuildConfig.CLOUD_PASSWORD
        if (!isAuthorized()) {
            authorize(email, password)
                ?: throw IllegalStateException("Unabled to login!")
        }
    }

    private fun isAuthorized() = Firebase.auth.currentUser != null

    private suspend fun authorize(email: String, password: String) =
        suspendCoroutine { continuation ->
            val auth = FirebaseAuth.getInstance(spreadsheetApp)
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { continuation.resume(it.user != null) }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
        }
}