package com.unh.personal_health_buddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.unh.personal_health_buddy.account.SignInScreen
import com.unh.personal_health_buddy.account.SignUpScreen
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme

/**
 * The main and only activity for this application.
 * It serves as the entry point and hosts all the Jetpack Compose UI content.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created. This is where most initialization should go.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initializes the Firebase SDK. This must be done before using any Firebase services.
        FirebaseApp.initializeApp(this)

        // Enables the app to draw content edge-to-edge, behind the system status and navigation bars.
        enableEdgeToEdge()

        // Sets the main UI content of the activity using Jetpack Compose.
        setContent {
            // Applies the custom theme (colors, typography, etc.) to the entire application.
            PersonalHealthBuddyTheme {
                // Creates and remembers a NavController, which manages screen navigation.
                val navController = rememberNavController()

                // Scaffold is a top-level Material Design layout component.
                // It provides slots for various UI elements and handles insets like the status bar.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // NavHost is the container that displays the current navigation destination.
                    NavHost(
                        navController = navController,
                        // "sign-in" is the route of the screen that is shown first when the app starts.
                        startDestination = "sign-in",
                        // Applies padding provided by the Scaffold to prevent content from being hidden by system bars.
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Defines a destination in the navigation graph with the route "welcome".
                        composable("welcome") {
                            WelcomeScreen(navController)
                        }
                        // Defines the "sign-up" destination.
                        composable("sign-up") {
                            SignUpScreen(navController = navController)
                        }
                        // Defines the "sign-in" destination.
                        composable("sign-in") {
                            SignInScreen(navController = navController)
                        }
                        // Defines the "home" destination, shown after successful login/signup.
                        composable("home") {
                            HomeScreen(navController = navController)
                        }
                        // Defines the "reset-password" destination.
                        composable("reset-password") {
                            ResetPasswordScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}

/**
 * A placeholder composable for the Welcome Screen.
 */
@Composable
fun WelcomeScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Welcome Screen")
    }
}

/**
 * A placeholder composable for the Home Screen.
 */
@Composable
fun HomeScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Home Screen")
    }
}

/**
 * A placeholder composable for the Reset Password Screen.
 */
@Composable
fun ResetPasswordScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Reset Password Screen")
    }
}