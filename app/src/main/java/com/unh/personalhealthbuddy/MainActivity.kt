package com.unh.personalhealthbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.unh.personalhealthbuddy.screen.MainWelcomeScreen
import com.unh.personalhealthbuddy.ui.theme.PersonalHealthBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Your MainWelcomeScreen will be launched immediately here
            // FIX: Use the corrected theme name without underscores
            PersonalHealthBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // This is your main starting screen:
                    MainWelcomeScreen()
                }
            }
        }
    }
}