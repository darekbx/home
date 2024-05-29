package com.darekbx.geotracker.firebase

import com.darekbx.geotracker.BuildConfig
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthenticationUtils {

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
            Firebase.auth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { continuation.resume(it.user) }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
        }

}