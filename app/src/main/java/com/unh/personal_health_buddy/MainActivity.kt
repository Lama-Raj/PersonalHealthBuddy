package com.unh.personal_health_buddy

import TempProfileStorage
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
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

        // Edge-to-edge layout
        enableEdgeToEdge()

        Log.d("MainActivity", "onCreate called")

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current

            // Force Light Mode
            PersonalHealthBuddyTheme(darkTheme = false) {

                // One-time Firebase + user data load
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

                                // Prefer temp profile bitmap if available
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

                // Root overlay: app content + top nudge bar
                Box {
                    // Main navigation / screens
                    SetupAuthentication(
                        activity = this@MainActivity,
                        navController = navController,
                    )

                    // Global top notification bar (random nudges)
                    RandomTopNudgeHost(
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}
