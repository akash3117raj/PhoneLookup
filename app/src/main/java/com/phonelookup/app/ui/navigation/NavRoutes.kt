package com.phonelookup.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.phonelookup.app.data.local.SessionManager
import com.phonelookup.app.ui.screens.AdminDashboardScreen
import com.phonelookup.app.ui.screens.DashboardScreen
import com.phonelookup.app.ui.screens.LoginScreen
import com.phonelookup.app.ui.screens.SplashScreen

/** Navigation route constants */
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val ADMIN_DASHBOARD = "admin_dashboard"
}

@Composable
fun AppNavigation(sessionManager: SessionManager) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = {
            fadeIn(tween(300)) + slideInHorizontally(
                initialOffsetX = { it / 3 },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(tween(200))
        },
        popEnterTransition = {
            fadeIn(tween(300)) + slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(tween(200)) + slideOutHorizontally(
                targetOffsetX = { it / 3 },
                animationSpec = tween(200)
            )
        }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                sessionManager = sessionManager,
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                sessionManager = sessionManager,
                onLoginSuccess = { isAdmin ->
                    val destination = if (isAdmin) Routes.ADMIN_DASHBOARD else Routes.DASHBOARD
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                sessionManager = sessionManager,
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboardScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true }
                    }
                }
            )
        }
    }
}
