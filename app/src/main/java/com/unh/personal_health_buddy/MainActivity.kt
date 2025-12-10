package com.unh.personal_health_buddy

import TempProfileStorage
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.unh.personal_health_buddy.Authentication.FirestoreHelper
import com.unh.personal_health_buddy.database.UserDataCache
import com.unh.personal_health_buddy.firebase.SetupAuthentication
import com.unh.personal_health_buddy.notifications.RandomTopNudgeHost
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge
        enableEdgeToEdge()

        Log.d("MainActivity", "onCreate called")

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current

            // Observe current route from NavController
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // 🔹 All the routes where we DO NOT want the top nudge bar
            //    Adjust these strings to match your actual auth routes.
            val authRoutes = setOf(
                "welcome",
                "sign-in",
                "sign-up",
                "reset-password"
            )

            // Force Light Mode
            PersonalHealthBuddyTheme(darkTheme = false) {

                // Preload user data once
                LaunchedEffect(Unit) {
                    FirebaseApp.initializeApp(context)

                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null && !UserDataCache.isDataLoaded) {
                        withContext(Dispatchers.IO) {
                            try {
                                Log.d("MainActivity", "Fetching user data...")
                                UserDataCache.user = FirestoreHelper.getUser(uid)
                                UserDataCache.emergencyContacts =
                                    FirestoreHelper.readAllEmergencyContacts()
                                UserDataCache.healthInfo =
                                    FirestoreHelper.getHealthInformation()

                                val tempBitmap = TempProfileStorage.tempProfileBitmap
                                if (tempBitmap != null) {
                                    UserDataCache.profileBitmap = tempBitmap
                                } else {
                                    UserDataCache.user?.profileImageUrl?.let { url ->
                                        try {
                                            val stream = URL(url).openStream()
                                            UserDataCache.profileBitmap =
                                                BitmapFactory.decodeStream(stream)
                                        } catch (e: Exception) {
                                            Log.e(
                                                "MainActivity",
                                                "Error loading profile image: ${e.message}"
                                            )
                                        }
                                    }
                                }

                                UserDataCache.isDataLoaded = true
                                Log.d("MainActivity", "User data cached successfully")
                            } catch (e: Exception) {
                                Log.e("MainActivity", "Error loading user data: ${e.message}")
                            }
                        }
                    }
                }

                // Root layout: app content + optional top nudge bar
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Your navigation graph (welcome, login, home, etc.)
                    SetupAuthentication(
                        activity = this@MainActivity,
                        navController = navController,
                    )

                    // 🔹 Show RandomTopNudgeHost ONLY when NOT on auth routes
                    if (currentRoute != null && currentRoute !in authRoutes) {
                        RandomTopNudgeHost(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .statusBarsPadding()
                        )
                    }
                }
            }
        }
    }
}
