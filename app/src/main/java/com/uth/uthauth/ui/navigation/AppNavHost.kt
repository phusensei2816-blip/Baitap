package com.uth.uthauth.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.uth.uthauth.UthAuthApp
import com.uth.uthauth.ui.add.AddTaskRoute
import com.uth.uthauth.ui.auth.LoginRoute
import com.uth.uthauth.ui.detail.DetailRoute
import com.uth.uthauth.ui.home.HomeRoute
import com.uth.uthauth.ui.list.ListRoute
import com.uth.uthauth.ui.notifications.NotificationsRoute
import com.uth.uthauth.ui.profile.ProfileRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    val context = LocalContext.current
    DisposableEffect(navController) {
        val telemetry = (context.applicationContext as UthAuthApp).container.telemetry
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            destination.route?.let(telemetry::logScreen)
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }

    // Các điểm đến dùng chung giữa nhiều màn hình
    val openTask: (String) -> Unit = { id -> navController.navigate(Screen.Detail.createRoute(id)) }
    val openProfile: () -> Unit = { navController.navigate(Screen.Profile.route) }
    val openAdd: () -> Unit = { navController.navigate(Screen.Add.route) }
    val openList: () -> Unit = { navController.navigate(Screen.List.route) { launchSingleTop = true } }
    val openNotifications: () -> Unit =
        { navController.navigate(Screen.Notifications.route) { launchSingleTop = true } }
    val backToHome: () -> Unit = { navController.popBackStack(Screen.Home.route, inclusive = false) }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
            LoginRoute(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeRoute(
                onOpenProfile = openProfile,
                onOpenList = openList,
                onOpenNotifications = openNotifications,
                onOpenAdd = openAdd,
                onOpenTask = openTask,
            )
        }

        composable(Screen.List.route) {
            ListRoute(
                onOpenTask = openTask,
                onOpenHome = backToHome,
                onOpenNotifications = openNotifications,
                onOpenAdd = openAdd,
                onOpenProfile = openProfile,
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsRoute(
                onOpenHome = backToHome,
                onOpenList = openList,
                onOpenAdd = openAdd,
                onOpenProfile = openProfile,
            )
        }

        composable(Screen.Add.route) {
            AddTaskRoute(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument(Screen.Detail.ARG_TASK_ID) { type = NavType.StringType })
        ) {
            DetailRoute(onBack = { navController.popBackStack() })
        }

        composable(Screen.Profile.route) {
            ProfileRoute(
                onBack = { navController.popBackStack() },
                onSignedOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
    }
}
