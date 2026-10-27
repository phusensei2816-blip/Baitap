package com.example.emailchange.GiaoDienUI

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResetPasswordScreen(onBack: () -> Unit, onNext: (String) -> Unit) {
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    val mismatch = confirm.isNotEmpty() && password != confirm
    val valid = password.length >= 6 && password == confirm

    AuthLayout(
        title = "Create new password",
        subtitle = "Your new password must be different form previously used password",
        onBack = onBack
    ) {
        AppTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Password",
            icon = Icons.Default.Lock,
            isPassword = true,
            keyboardType = KeyboardType.Password
        )
        Spacer(Modifier.height(16.dp))
        AppTextField(
            value = confirm,
            onValueChange = { confirm = it },
            placeholder = "Confirm Password",
            icon = Icons.Default.Lock,
            isPassword = true,
            keyboardType = KeyboardType.Password
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Password must be at least 6 characters ",
            color = Color.Red,
            fontSize = 12.sp
        )
        if (mismatch) {
            Spacer(Modifier.height(8.dp))
            Text("Passwords do not match", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Next", enabled = valid) { onNext(password) }
    }
}