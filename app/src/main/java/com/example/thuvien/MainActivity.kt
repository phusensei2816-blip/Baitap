package com.example.thuvien

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.thuvien.ui.MainScreen
import com.example.thuvien.ui.theme.ThuVienTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThuVienTheme {
                MainScreen()
            }
        }
    }
}
