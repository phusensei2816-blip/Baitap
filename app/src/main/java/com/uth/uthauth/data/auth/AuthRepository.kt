package com.uth.uthauth.data.auth

import com.uth.uthauth.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: AuthUser?
    fun observeUser(): Flow<AuthUser?>
    suspend fun signInWithGoogle(idToken: String): Result<AuthUser>
    suspend fun signInAnonymously(): Result<AuthUser>
    suspend fun signOut()
}
