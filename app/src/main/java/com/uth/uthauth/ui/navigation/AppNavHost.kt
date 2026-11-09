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
import com.uth.uthauth.ui.auth.LoginRoute
import com.uth.uthauth.ui.detail.DetailRoute
import com.uth.uthauth.ui.home.HomeRoute
import com.uth.uthauth.ui.list.ListRoute
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

    val openTask: (String) -> Unit = { id -> navController.navigate(Screen.Detail.createRoute(id)) }
    val openProfile: () -> Unit = { navController.navigate(Screen.Profile.route) }

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
                onOpenList = { navController.navigate(Screen.List.route) { launchSingleTop = true } },
                onOpenTask = openTask,
            )
        }

        composable(Screen.List.route) {
            ListRoute(
                onOpenTask = openTask,
                onOpenHome = { navController.popBackStack(Screen.Home.route, inclusive = false) },
                onOpenProfile = openProfile,
            )
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
