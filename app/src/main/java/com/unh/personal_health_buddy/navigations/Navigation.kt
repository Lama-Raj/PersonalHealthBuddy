package com.unh.personal_health_buddy.navigations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.account.ResetPasswordScreen
import com.unh.personal_health_buddy.account.SignInScreen
import com.unh.personal_health_buddy.account.SignUpScreen
import com.unh.personal_health_buddy.screen.DashboardScreen
import com.unh.personal_health_buddy.screen.MainWelcomeScreen

// This composable function sets up the application's navigation graph.

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Screens that should not have the BottomNavBar
    val screensWithoutNavBar = listOf("main_welcome", "signup", "sign-in", "reset-password")

    Scaffold(
        bottomBar = {
            if (currentRoute !in screensWithoutNavBar) {
                BottomNavBar(
                    currentRoute = currentRoute ?: "",
                    onItemClick = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main_welcome",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("main_welcome") { MainWelcomeScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("sign-in") { SignInScreen(navController) }
            composable("reset-password") { ResetPasswordScreen(navController) }

            // Main app destinations with BottomNavBar
            composable("home") { DashboardScreen(navController) }
            composable("map_route") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Map Screen")
                }
            }
            composable("notification_route") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Notification Screen")
                }
            }
            composable("profile_route") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Profile Screen")
                }
            }
        }
    }
}
