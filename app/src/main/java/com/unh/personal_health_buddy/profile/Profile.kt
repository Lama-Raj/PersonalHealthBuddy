package com.unh.personal_health_buddy.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unh.personal_health_buddy.R
import com.unh.personal_health_buddy.account.LogoutConfirmationDialog
import com.unh.personal_health_buddy.database.UserDataCache
import com.unh.personal_health_buddy.notifications.NotificationSettings
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.LightBlueBackground
import com.unh.personal_health_buddy.ui.theme.TextColor
import com.unh.personal_health_buddy.ui.theme.White

/*
* Updated the Code using Chat GTP as there was certain errors in the original code.
* Reference Code: YouTube & Chat GPT, Gemini and Copilot
* Reference : Stack Overflow, GitHub, W3Schools
*
* */
sealed class ProfileItem(val title: String, val icon: ImageVector, val route: String) {
    object Account : ProfileItem("Account", Icons.Filled.Person, "account")
    object FAQS : ProfileItem("FAQs & Help", Icons.Filled.Chat, "faqs")
    object Logout : ProfileItem("Logout", Icons.AutoMirrored.Filled.ExitToApp, "logout")
}

val profileItems = listOf(
    ProfileItem.Account,
    ProfileItem.FAQS,
    ProfileItem.Logout
)

// ------------------- Profile Screen -------------------
@Composable
fun ProfileScreen(
    navController: NavHostController,
    items: List<ProfileItem>,
    currentRoute: String
) {
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf(UserDataCache.user?.firstname ?: "User") }
    var profileBitmap by remember { mutableStateOf(UserDataCache.profileBitmap) }

    // Medicate-style theme colors
    val primaryBlue = Color(0xFF1877F2)
    val lightBlueBackground = Color(0xFFF3F6FF)

    // 🔹 Local reference to global toggle
    val isTopNudgeEnabledState = NotificationSettings.isTopNudgeEnabled

    // Fetch firstname and profile image URL from Firestore
    LaunchedEffect(true) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    firstName = document.getString("firstname") ?: "User"
                    profileImageUrl = document.getString("profileImageUrl")
                }
        }
    }

    // Update UI when cache is loaded (optional)
    LaunchedEffect(UserDataCache.isDataLoaded) {
        if (UserDataCache.isDataLoaded) {
            firstName = UserDataCache.user?.firstname ?: "User"
            profileBitmap = UserDataCache.profileBitmap
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        lightBlueBackground,
                        Color.White
                    )
                )
            )
    ) {

        // ---------- Top Header + Profile Info ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = primaryBlue
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Manage your account and preferences",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextColor.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Profile row (image + text) – no separate card
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            lightBlueBackground,
                            CircleShape
                        )
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            primaryBlue.copy(alpha = 0.4f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileBitmap != null) {
                        Image(
                            bitmap = profileBitmap!!.asImageBitmap(),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(82.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile_picture),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(82.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = firstName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = primaryBlue
                    )

                    Text(
                        text = "Signed in to Personal Health Buddy",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextColor.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .offset(y = 8.dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(lightBlueBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 24.dp,
                        bottom = 32.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "Account & Preferences",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = TextColor.copy(alpha = 0.75f),
                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                )

                // 1) Account
                ProfileRow(
                    title = ProfileItem.Account.title,
                    subtitle = "Edit personal info and health details",
                    icon = ProfileItem.Account.icon,
                    primaryBlue = primaryBlue
                ) {
                    navController.navigate("account") {
                        launchSingleTop = true
                    }
                }

                // 2) FAQs & Help
                ProfileRow(
                    title = ProfileItem.FAQS.title,
                    subtitle = "Get answers and contact support",
                    icon = ProfileItem.FAQS.icon,
                    primaryBlue = primaryBlue
                ) {
                    navController.navigate("faqs") {
                        launchSingleTop = true
                    }
                }

                // 3) Health reminder toggle (between FAQ and Logout)
                ReminderToggleRow(
                    isEnabled = isTopNudgeEnabledState.value,
                    onToggle = { isOn ->
                        isTopNudgeEnabledState.value = isOn
                    },
                    primaryBlue = primaryBlue
                )

                // 4) Logout
                ProfileRow(
                    title = ProfileItem.Logout.title,
                    subtitle = "Sign out of this device",
                    icon = ProfileItem.Logout.icon,
                    primaryBlue = primaryBlue
                ) {
                    showLogoutDialog = true
                }
            }
        }

        // ---------- Logout Dialog ----------
        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onConfirm = {
                    showLogoutDialog = false
                    navController.navigate("welcome") { popUpTo(0) }
                },
                onCancel = { showLogoutDialog = false }
            )
        }
    }
}

// Reusable row for normal profile items
@Composable
private fun ProfileRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    primaryBlue: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(primaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color(0xFFE9F2FF),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = TextColor
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextColor.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go",
            tint = Color(0xFF4B5563)
        )
    }
}

// Special row for the switch
@Composable
private fun ReminderToggleRow(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    primaryBlue: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(primaryBlue.copy(alpha = 0.95f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Health reminders",
                tint = Color(0xFFE9F2FF),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Health reminders",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = TextColor
            )
            Text(
                text = "Show reminder bar at the top of the app",
                style = MaterialTheme.typography.bodySmall,
                color = TextColor.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
    }
}

// ------------------- Preview -------------------
@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    val navController = rememberNavController()
    ProfileScreen(
        navController = navController,
        items = profileItems,
        currentRoute = "profile"
    )
}
