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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.LightBlueBackground
import com.unh.personal_health_buddy.ui.theme.TextColor
import com.unh.personal_health_buddy.ui.theme.White

// ------------------- Profile Items -------------------
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

    // Update UI when cache is loaded
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

        // ---------- Top Header + Profile Area ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header title
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

            Spacer(modifier = Modifier.height(16.dp))

            // Avatar + name + subtitle (no card, blended with background)
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
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
                                    .size(100.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.profile_picture),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(100.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = firstName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = primaryBlue
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Signed in to Personal Health Buddy",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextColor.copy(alpha = 0.7f)
                    )
                }
            }

            // Add some space so subtitle is clearly above the bottom sheet
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ---------- Bottom Section with Profile Items ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(lightBlueBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 28.dp,
                        bottom = 32.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "Account & Help",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = TextColor.copy(alpha = 0.75f),
                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                )

                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(White)
                            .clickable {
                                when (item) {
                                    is ProfileItem.Account -> {
                                        navController.navigate("account") {
                                            launchSingleTop = true
                                        }
                                    }

                                    is ProfileItem.FAQS -> {
                                        navController.navigate("faqs") {
                                            launchSingleTop = true
                                        }
                                    }

                                    is ProfileItem.Logout -> {
                                        showLogoutDialog = true
                                    }
                                }
                            }
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
                                item.icon,
                                contentDescription = item.title,
                                tint = Color(0xFFE9F2FF),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = TextColor
                            )

                            val subtitle = when (item) {
                                is ProfileItem.Account -> "Edit personal info and health details"
                                is ProfileItem.FAQS -> "Get answers and contact support"
                                is ProfileItem.Logout -> "Sign out of this device"
                            }

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
