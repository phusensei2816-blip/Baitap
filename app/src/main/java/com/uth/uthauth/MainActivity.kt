package com.uth.uthauth

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.uth.uthauth.ui.navigation.AppNavHost
import com.uth.uthauth.ui.navigation.Screen
import com.uth.uthauth.ui.theme.UthAuthTheme

// AppCompatActivity: cần để Per-app language hoạt động trên Android < 13
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as UthAuthApp).container

        setContent {
            UthAuthTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    // Đã đăng nhập từ trước -> vào thẳng trang chủ
                    val startDestination = remember {
                        if (container.authRepository.currentUser != null) Screen.Home.route
                        else Screen.Login.route
                    }
                    AppNavHost(navController = navController, startDestination = startDestination)
                }
            }
        }
    }
}
