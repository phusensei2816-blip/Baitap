package com.example.emailchange.GiaoDienUI

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emailchange.model.ResetInfo

@Composable
fun ForgotPasswordScreen(received: ResetInfo, onNext: (String) -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    val valid = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    AuthLayout(
        title = "Forgot Password?",
        subtitle = "Enter your Email, we will send you a verification code."
    ) {
        AppTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Your Email",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Next", enabled = valid) { onNext(email.trim()) }

        if (!received.isEmpty) {
            Spacer(Modifier.height(32.dp))
            ReceivedDataBox(received)
        }
    }
}

@Composable
private fun ReceivedDataBox(info: ResetInfo) {
    val red = Color(0xFFD32F2F)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFE3EEFB))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(info.email, color = red, fontSize = 18.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
            Text(info.otp, color = red, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(info.password, color = red, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
    }
}