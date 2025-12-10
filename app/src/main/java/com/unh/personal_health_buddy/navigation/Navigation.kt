package com.unh.personal_health_buddy.navigation

import AccountFormScreen
import AccountScreen
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.unh.personal_health_buddy.account.ResetPasswordDialog
import com.unh.personal_health_buddy.account.SignInScreen
import com.unh.personal_health_buddy.account.SignUpScreen
import com.unh.personal_health_buddy.chat.AiChatScreen
import com.unh.personal_health_buddy.contacts.EmergencyContactScreen
import com.unh.personal_health_buddy.features.BloodGroupScreen
import com.unh.personal_health_buddy.features.BmiScreen
import com.unh.personal_health_buddy.features.MedicateScreen
import com.unh.personal_health_buddy.map.GoogleMapScreen
import com.unh.personal_health_buddy.notifications.NotificationScreen
import com.unh.personal_health_buddy.profile.FAQScreen
import com.unh.personal_health_buddy.profile.ProfileScreen
import com.unh.personal_health_buddy.profile.profileItems
import com.unh.personal_health_buddy.screens.HomeScreen
import com.unh.personal_health_buddy.screens.MainWelcomeScreen
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.MediumGray

// DATA CLASS
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

// BOTTOM NAV ITEMS
val bottomNavItems = listOf(
    BottomNavItem("home", Icons.Filled.Home, "Home"),
    BottomNavItem("map", Icons.Filled.LocationOn, "Map"),
    BottomNavItem("notifications", Icons.Filled.Notifications, "Notification"),
    BottomNavItem("profile", Icons.Filled.Person, "Profile")
)

// SCREENS THAT SHOULD HIDE BOTTOM NAV
val screensWithoutBottomNav = setOf(
    "welcome",
    "sign-in",
    "sign-up",
    "reset-password",
)

// APP NAVIGATION
@Composable
fun AppNavigation(
    navController: NavHostController,
    googleSignInClient: GoogleSignInClient,
    launcher: ActivityResultLauncher<Intent>,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Feature screens that live "under" the Home tab
    val featureScreens = listOf(
        "bmi_screen",
        "blood_group_screen",
        "medicates_screen",
        "emergency_screen",
        "chat_ai_screen"
    )

    // If we're on a feature screen, treat the selected tab as "home"
    val routeForNavBar = if (currentRoute in featureScreens) "home" else currentRoute

    Scaffold(
        bottomBar = {
            // Show bottom nav only if current route is NOT in the exclusion list
            if (currentRoute !in screensWithoutBottomNav) {
                NavigationBar(
                    containerColor = Color.White,
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = routeForNavBar == item.route

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (item.route == "home") {
                                    // If user taps Home, always go back to home screen
                                    navController.popBackStack("home", inclusive = false)
                                } else {
                                    // Standard navigation for other tabs
                                    navController.navigate(item.route) {
                                        // Pop up to home to avoid building large stack
                                        popUpTo("home") {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
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
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            // AUTH SCREENS
            composable("welcome") { MainWelcomeScreen(navController) }
            composable("sign-in") { SignInScreen(navController, googleSignInClient, launcher) }
            composable("sign-up") { SignUpScreen(navController, googleSignInClient, launcher) }
            composable("reset-password") {
                ResetPasswordDialog(navController, onDismiss = { navController.popBackStack() })
            }

            // MAIN SCREENS (with bottom nav)
            composable("home") { HomeScreen(navController) }
            composable("map") { GoogleMapScreen(navController) }
            composable("notifications") { NotificationScreen(navController) }
            composable("profile") { ProfileScreen(navController, profileItems, "profile") }

            // FEATURE SCREENS (still show bottom nav, Home highlighted)
            composable("blood_group_screen") { BloodGroupScreen(navController) }
            composable("medicates_screen") { MedicateScreen(navController) }
            composable("emergency_screen") { EmergencyContactScreen(navController) }
            composable("bmi_screen") { BmiScreen(navController) }
            composable("chat_ai_screen") { AiChatScreen(navController) }

            //PROFILE SUB-SCREENS (no bottom nav)
            composable("account") { AccountScreen(navController) }
            composable("account-form") { AccountFormScreen(navController) }
            composable("FAQS") { FAQScreen(navController) }
        }
    }
}
