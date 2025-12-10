package com.unh.personal_health_buddy

import TempProfileStorage
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.unh.personal_health_buddy.Authentication.FirestoreHelper
import com.unh.personal_health_buddy.database.UserDataCache
import com.unh.personal_health_buddy.firebase.SetupAuthentication
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.unh.personal_health_buddy.notifications.RandomTopNudgeHost

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge (status/navigation bar) layout
        enableEdgeToEdge()

        Log.d("MainActivity", "onCreate called")

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current

            // Force light mode for now
            PersonalHealthBuddyTheme(darkTheme = false) {

                // Initialize Firebase + warm up user cache
                LaunchedEffect(Unit) {
                    FirebaseApp.initializeApp(context)

                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null && !UserDataCache.isDataLoaded) {
                        withContext(Dispatchers.IO) {
                            try {
                                Log.d("MainActivity", "Fetching user data...")

                                // Load user core info
                                UserDataCache.user = FirestoreHelper.getUser(uid)
                                UserDataCache.emergencyContacts = FirestoreHelper.readAllEmergencyContacts()
                                UserDataCache.healthInfo = FirestoreHelper.getHealthInformation()

                                // Profile image: prefer temporary bitmap if set
                                val tempBitmap = TempProfileStorage.tempProfileBitmap
                                if (tempBitmap != null) {
                                    UserDataCache.profileBitmap = tempBitmap
                                } else {
                                    UserDataCache.user?.profileImageUrl?.let { url ->
                                        try {
                                            val stream = URL(url).openStream()
                                            UserDataCache.profileBitmap = BitmapFactory.decodeStream(stream)
                                        } catch (e: Exception) {
                                            Log.e("MainActivity", "Error loading profile image: ${e.message}")
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

                // Root layout: app content + random top nudge overlay
                Box(Modifier.fillMaxSize()) {
                    SetupAuthentication(
                        activity = this@MainActivity,
                        navController = navController,
                    )

                    // Global random health nudges shown at the top of the app
                    RandomTopNudgeHost(
                        modifier = androidx.compose.ui.Modifier
                            .align(androidx.compose.ui.Alignment.TopCenter)
                    )
                }
            }
        }
    }
}
