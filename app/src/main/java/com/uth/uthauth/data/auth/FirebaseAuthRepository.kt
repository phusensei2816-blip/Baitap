package com.uth.uthauth.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.uth.uthauth.domain.model.AuthUser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override val currentUser: AuthUser?
        get() = auth.currentUser?.toDomain()

    override fun observeUser(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.toDomain()) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthUser> = safeCall {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        result.user?.toDomain() ?: error("Không lấy được thông tin người dùng")
    }

    override suspend fun signInAnonymously(): Result<AuthUser> = safeCall {
        val result = auth.signInAnonymously().await()
        result.user?.toDomain() ?: error("Không lấy được thông tin người dùng")
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    private inline fun <T> safeCall(block: () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    private fun FirebaseUser.toDomain() = AuthUser(
        uid = uid,
        displayName = displayName,
        email = email,
        photoUrl = photoUrl?.toString(),
        isAnonymous = isAnonymous,
        isGoogleLinked = providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID },
    )
}
