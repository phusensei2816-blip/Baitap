package com.example.emailchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.emailchange.navigation.ResetNavGraph
import com.example.emailchange.ui.theme.EmailChangeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmailChangeTheme {
                ResetNavGraph()
            }
        }
    }
}