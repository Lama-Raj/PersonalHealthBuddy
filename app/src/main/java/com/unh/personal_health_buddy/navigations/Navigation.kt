package com.unh.personal_health_buddy.navigations


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.account.SignUpScreen

// This composable function sets up the application's navigation graph.

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "signup") {
        composable("signup") { SignUpScreen(navController) }
        composable("sign-in") { Text("Sign In Screen") }
        composable("home") { Text("Home Screen") }
        composable("welcome") { Text("Welcome Screen") }
    }
}