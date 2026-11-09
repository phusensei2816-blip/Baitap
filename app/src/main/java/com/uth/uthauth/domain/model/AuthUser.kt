package com.uth.uthauth.domain.model

data class AuthUser(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?,
    val isAnonymous: Boolean,
    val isGoogleLinked: Boolean,
) {
    /** Tên hiển thị; null nếu là khách (UI tự dùng chuỗi "Khách"/"Guest" theo ngôn ngữ). */
    val nameOrNull: String?
        get() = displayName?.takeIf { it.isNotBlank() }
            ?: if (!isAnonymous) email?.substringBefore('@') else null

    /** "Minh Anh" -> "MA" */
    fun initials(fallback: String): String {
        val parts = (nameOrNull ?: return fallback).trim().split(" ").filter { it.isNotEmpty() }
        return when {
            parts.isEmpty() -> fallback
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "${parts.first().first()}${parts.last().first()}".uppercase()
        }
    }
}
