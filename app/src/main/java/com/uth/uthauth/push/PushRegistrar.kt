package com.uth.uthauth.push

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.uth.uthauth.data.telemetry.AppTelemetry
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

/** Lưu FCM token vào Firestore (users/{uid}) và đăng ký topic "all". */
class PushRegistrar(
    private val db: FirebaseFirestore,
    private val telemetry: AppTelemetry,
) {
    suspend fun register(uid: String) {
        try {
            val messaging = FirebaseMessaging.getInstance()
            saveToken(uid, messaging.token.await())
            messaging.subscribeToTopic(TOPIC_ALL).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            telemetry.recordError(e)
        }
    }

    fun saveToken(uid: String, token: String) {
        Log.d(TAG, "FCM token = $token")
        db.collection("users").document(uid).set(
            mapOf("fcmToken" to token, "tokenUpdatedAt" to Timestamp.now()),
            SetOptions.merge()
        )
    }

    private companion object {
        const val TAG = "PushRegistrar"
        const val TOPIC_ALL = "all"
    }
}
