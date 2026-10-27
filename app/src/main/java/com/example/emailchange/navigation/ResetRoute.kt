package com.example.emailchange.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ResetRoute {
    @Serializable
    data class Forgot(
        val email : String="",
        val otp: String = "",
        val password: String = ""
    ): ResetRoute()
    @Serializable
    data class Verify(val email: String) : ResetRoute()

    @Serializable
    data class NewPassword(val email: String, val otp: String) : ResetRoute()

    @Serializable
    data class Confirm(val email: String, val otp: String, val password: String) : ResetRoute()
}