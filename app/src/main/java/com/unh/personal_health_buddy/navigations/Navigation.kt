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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.account.ResetPasswordScreen
import com.unh.personal_health_buddy.account.SignInScreen
import com.unh.personal_health_buddy.account.SignUpScreen
import com.unh.personal_health_buddy.features.BloodGroupScreen
import com.unh.personal_health_buddy.features.BmiScreen
import com.unh.personal_health_buddy.features.EmergencyContactScreen
import com.unh.personal_health_buddy.features.MedicateScreen
import com.unh.personal_health_buddy.screen.DashboardScreen
import com.unh.personal_health_buddy.screen.MainWelcomeScreen
import com.unh.personal_health_buddy.screen.NotificationScreen

// This composable function sets up the application's navigation graph.

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Screens that should not have the BottomNavBar
    val screensWithoutNavBar = listOf("main_welcome", "signup", "sign-in", "reset-password")

    // Feature screens that are children of the "home" tab
    val featureScreens = listOf("bmi_screen", "blood_group_screen", "medicates_screen", "emergency_screen", "chat_ai_screen")

    // Determine the route to highlight on the nav bar. If we're on a feature screen, highlight "home".
    val routeForNavBar = if (currentRoute in featureScreens) "home" else currentRoute

    Scaffold(
        bottomBar = {
            if (currentRoute !in screensWithoutNavBar) {
                BottomNavBar(
                    currentRoute = routeForNavBar ?: "home",
                    onItemClick = { route ->
                        if (route == "home") {
                            // Special handling for the home button to always pop the stack
                            navController.popBackStack("home", inclusive = false)
                        } else {
                            // Standard navigation for other tabs
                            navController.navigate(route) {
                                popUpTo("home") {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("main_welcome") { MainWelcomeScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("sign-in") { SignInScreen(navController) }
            composable("reset-password") { ResetPasswordScreen(navController) }

            // Main app destinations
            composable("home") { DashboardScreen(navController) }
            composable("map_route") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Map Screen")
                }
            }
            composable("notification_route") { NotificationScreen(navController)

            }
            composable("profile_route") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Profile Screen")
                }
            }

            // Feature screen destinations
            composable("bmi_screen") {BmiScreen(navController)
            }
            //blood_group_screen
            composable("blood_group_screen") {BloodGroupScreen(navController)
            }
            // Medicates Screen
            composable("medicates_screen") {MedicateScreen(navController)
            }
            // Emergency Contact Screen
            composable("emergency_screen") {EmergencyContactScreen(navController)
            }
            composable("chat_ai_screen") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chat With AI Screen")
                }
            }
        }
    }
}
