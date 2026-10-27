package com.example.emailchange.GiaoDienUI

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.emailchange.model.ResetInfo

@Composable
fun ConfirmScreen(info: ResetInfo, onBack: () -> Unit, onSubmit: () -> Unit) {
    AuthLayout(
        title = "Confirm",
        subtitle = "We are here to help you!",
        onBack = onBack
    ) {
        AppTextField(info.email, {}, "Email", Icons.Default.Person, readOnly = true)
        Spacer(Modifier.height(16.dp))
        AppTextField(info.otp, {}, "OTP", Icons.Default.Email, readOnly = true)
        Spacer(Modifier.height(16.dp))
        AppTextField(info.maskedPassword(), {}, "Password", Icons.Default.Lock, readOnly = true)
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Submit", onClick = onSubmit)
    }
}