package com.unh.personal_health_buddy

import TempProfileStorage
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make system navigation bar transparent and handle edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        Log.d("MainActivity", "onCreate called")

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current

            PersonalHealthBuddyTheme {
                LaunchedEffect(Unit) {
                    FirebaseApp.initializeApp(context)

                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null && !UserDataCache.isDataLoaded) {
                        withContext(Dispatchers.IO) {
                            try {
                                Log.d("MainActivity", "Fetching user data...")
                                UserDataCache.user = FirestoreHelper.getUser(uid)
                                UserDataCache.emergencyContacts = FirestoreHelper.readAllEmergencyContacts()
                                UserDataCache.healthInfo = FirestoreHelper.getHealthInformation()

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

                SetupAuthentication(
                    activity = this@MainActivity,
                    navController = navController,
                )
            }
        }
    }
}