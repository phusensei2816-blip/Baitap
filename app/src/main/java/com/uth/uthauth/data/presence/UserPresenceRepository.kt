package com.uth.uthauth.data.presence

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.uth.uthauth.data.telemetry.AppTelemetry
import com.uth.uthauth.domain.model.AuthUser

/**
 * Realtime Database:
 *  - users/{uid}/profile : thông tin + lastLoginAt
 *  - status/{uid}        : trạng thái online/offline (presence, tự về offline khi mất kết nối)
 */
class UserPresenceRepository(private val telemetry: AppTelemetry) {

    private var startedFor: String? = null
    private var connectedRef: DatabaseReference? = null
    private var listener: ValueEventListener? = null

    fun start(user: AuthUser) {
        if (startedFor == user.uid) return
        startedFor = user.uid
        runCatching {
            val db = FirebaseDatabase.getInstance()

            db.getReference("users/${user.uid}/profile").updateChildren(
                mapOf(
                    "name" to user.nameOrNull,
                    "email" to user.email,
                    "isAnonymous" to user.isAnonymous,
                    "lastLoginAt" to ServerValue.TIMESTAMP,
                )
            )

            val statusRef = db.getReference("status/${user.uid}")
            val ref = db.getReference(".info/connected")
            val l = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.getValue(Boolean::class.java) == true) {
                        statusRef.onDisconnect().setValue(offline())
                        statusRef.setValue(online())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    telemetry.recordError(error.toException())
                }
            }
            ref.addValueEventListener(l)
            connectedRef = ref
            listener = l
        }.onFailure {
            // Thường do chưa tạo Realtime Database hoặc google-services.json thiếu firebase_url
            telemetry.recordError(it)
            startedFor = null
        }
    }

    fun stop() {
        val uid = startedFor ?: return
        runCatching {
            listener?.let { connectedRef?.removeEventListener(it) }
            FirebaseDatabase.getInstance().getReference("status/$uid").setValue(offline())
        }
        startedFor = null
        listener = null
        connectedRef = null
    }

    private fun online() = mapOf("online" to true, "lastSeen" to ServerValue.TIMESTAMP)
    private fun offline() = mapOf("online" to false, "lastSeen" to ServerValue.TIMESTAMP)
}
