package com.example.thuvien.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/** Mỗi tab là một lớp con của Screen (kế thừa). */
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Manage : Screen("manage", "Quản lý", Icons.Default.Home)
    object Books : Screen("books", "DS Sách", Icons.Default.Menu)
    object Students : Screen("students", "Sinh viên", Icons.Default.Person)
}

val bottomItems = listOf(Screen.Manage, Screen.Books, Screen.Students)
