package com.example.emailchange.GiaoDienUI

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VerifyCodeScreen(email: String, onBack: () -> Unit, onNext: (String) -> Unit) {
    var otp by rememberSaveable { mutableStateOf("") }

    AuthLayout(
        title = "Verify Code",
        subtitle = "Enter the code we just sent to\n$email",
        onBack = onBack
    ) {
        OtpInput(value = otp, onValueChange = { otp = it })
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Next", enabled = otp.length == 5) { onNext(otp) }
    }
}