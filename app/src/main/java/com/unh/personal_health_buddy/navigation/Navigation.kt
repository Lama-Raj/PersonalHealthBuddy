package com.unh.personal_health_buddy.navigation

import AccountFormScreen
import AccountScreen
import GoogleMapScreen
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.unh.personal_health_buddy.chat.AiChatScreen
import com.unh.personal_health_buddy.features.BloodGroupScreen
import com.unh.personal_health_buddy.features.BmiScreen
import com.unh.personal_health_buddy.contacts.EmergencyContactScreen
import com.unh.personal_health_buddy.screens.MedicateScreen
import com.unh.personal_health_buddy.notifications.NotificationScreen
import com.unh.personal_health_buddy.profile.ProfileScreen
import com.unh.personal_health_buddy.Account.ResetPasswordDialog
import com.unh.personal_health_buddy.Account.SignInScreen
import com.unh.personal_health_buddy.Account.SignUpScreen
import com.unh.personal_health_buddy.profile.profileItems
import com.unh.personal_health_buddy.screens.HomeScreen
import com.unh.personal_health_buddy.screens.MainWelcomeScreen

import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.MediumGray

// -------------------- DATA CLASS --------------------
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

// -------------------- BOTTOM NAV ITEMS --------------------
val bottomNavItems = listOf(
    BottomNavItem("home", Icons.Filled.Home, "Home"),
    BottomNavItem("map", Icons.Filled.LocationOn, "Map"),
    BottomNavItem("notifications", Icons.Filled.Notifications, "Notification"),
    BottomNavItem("profile", Icons.Filled.Person, "Profile")
)

// -------------------- SCREENS THAT SHOULD HIDE BOTTOM NAV --------------------
val screensWithoutBottomNav = setOf(
    "welcome",
    "sign-in",
    "sign-up",
    "reset-password",
    "account",
    "account-form",
    "chat_ai_screen",
    "faqs",
    "blood_group_screen",
    "medicates_screen",
    "emergency_screen",
    "logout"
)

// -------------------- APP NAVIGATION (SIMPLIFIED) --------------------
@Composable
fun AppNavigation(
    navController: NavHostController,
    googleSignInClient: GoogleSignInClient,
    launcher: ActivityResultLauncher<Intent>,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Show bottom nav only if current route is NOT in the exclusion list
            if (currentRoute !in screensWithoutBottomNav) {
                NavigationBar(
                    containerColor = Color.White,
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    // Pop up to home to avoid building large stack
                                    popUpTo("home") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ButtonBlue,
                                selectedTextColor = ButtonBlue,
                                unselectedIconColor = MediumGray,
                                unselectedTextColor = MediumGray
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "welcome",
            modifier = Modifier.padding(paddingValues)
        ) {
            // -------------------- AUTH SCREENS --------------------
            composable("welcome") { MainWelcomeScreen(navController) }
            composable("sign-in") { SignInScreen(navController, googleSignInClient, launcher) }
            composable("sign-up") { SignUpScreen(navController, googleSignInClient, launcher) }
            composable("reset-password") {
                ResetPasswordDialog(navController, onDismiss = { navController.popBackStack() })
            }

            // -------------------- MAIN SCREENS (with bottom nav) --------------------
            composable("home") { HomeScreen(navController) }
            composable("map") { GoogleMapScreen(navController) }
            composable("notifications") { NotificationScreen(navController) }
            composable("profile") { ProfileScreen(navController, profileItems, "profile") }

            // -------------------- FEATURE SCREENS (with bottom nav) --------------------
            composable("blood_group_screen") { BloodGroupScreen(navController) }
            composable("medicates_screen") { MedicateScreen(navController) }
            composable("emergency_screen") { EmergencyContactScreen(navController) }
            composable("bmi_screen") { BmiScreen(navController) }
            composable("chat_ai_screen") {AiChatScreen(navController) }

            // -------------------- PROFILE SUB-SCREENS (no bottom nav) --------------------
            composable("account") { AccountScreen(navController) }
            composable("account-form") { AccountFormScreen(navController) }




            // -------------------- OTHER SCREENS --------------------
            //composable("chat_ai_screen") { ChatAIScreen(navController) }
        }
    }
}