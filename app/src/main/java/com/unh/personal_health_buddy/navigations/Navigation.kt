package com.unh.personal_health_buddy.navigations

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.account.SignInScreen
import com.unh.personal_health_buddy.account.SignUpScreen
import com.unh.personal_health_buddy.screen.MainWelcomeScreen

// This composable function sets up the application's navigation graph.

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main_welcome") {
        composable("main_welcome") { MainWelcomeScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("sign-in") { SignInScreen(navController) }
        // TODO: Replace these with your actual Home and ResetPassword screens
        composable("home") { Text("Home Screen") }
        composable("reset-password") { Text("Reset Password Screen") }
    }
}
