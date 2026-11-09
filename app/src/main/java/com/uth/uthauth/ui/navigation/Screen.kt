package com.uth.uthauth.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object List : Screen("list")
    data object Profile : Screen("profile")

    data object Detail : Screen("detail/{taskId}") {
        const val ARG_TASK_ID = "taskId"
        fun createRoute(taskId: String) = "detail/$taskId"
    }
}
