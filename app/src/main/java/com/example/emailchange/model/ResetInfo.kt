package com.example.emailchange.model

data class ResetInfo(
    val email: String="",
    val otp: String="",
    val password: String=""
) {
    val isEmpty: Boolean get() = email.isEmpty() && otp.isEmpty() && password.isEmpty()
    fun maskedPassword(): String = "*".repeat(password.length)
}