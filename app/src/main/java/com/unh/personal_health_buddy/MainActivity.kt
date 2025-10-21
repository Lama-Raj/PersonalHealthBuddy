package com.unh.personal_health_buddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
import com.unh.personal_health_buddy.navigations.AppNavigation
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
                // Scaffold is a top-level Material Design layout component.
                // It provides slots for various UI elements and handles insets like the status bar.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // AppNavigation is the composable that sets up the navigation graph for the app.
                    AppNavigation()
                }
            }
        }
    }
}
