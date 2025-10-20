package com.unh.personalhealthbuddy.navigations


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unh.personalhealthbuddy.account.SignUpScreen

// This composable function sets up the application's navigation graph.
@Composable
fun navigation() {
    // Creates and remembers a NavController.
    // The NavController is the central API for the Navigation component
    // and is responsible for keeping track of the back stack of composable.
    val navController = rememberNavController()

    // NavHost is a container that displays the current destination from the navigation graph.
    NavHost(
        navController = navController, // The controller that will manage navigation within this host.
        startDestination = "login" // The route for the destination that is displayed on launch.
    ) {
        // Defines a single destination in the navigation graph.
        // The string "signup" is the unique route that identifies this destination.
        composable("signup") {
            // This is the composable UI that will be displayed for the "signup" route.
            // The navController is passed to the SignUp screen, allowing it to trigger navigation events.
            SignUpScreen(navController)
        }
    }
}