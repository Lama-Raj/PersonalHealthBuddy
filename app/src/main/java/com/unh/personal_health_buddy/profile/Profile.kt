package com.unh.personal_health_buddy.profile

import LogoutConfirmationDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                        LightBlueBackground,
                        Color(0xFFE6F3FB)
                    )
                )
            )
    ) {
        // ---------- Top Profile Card ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 48.dp, bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF5AA9E6), Color(0xFFD6EFFF))
                            ),
                            CircleShape
                        )
                        .clip(CircleShape)
                        .border(3.dp, Color.White.copy(alpha = 0.9f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileBitmap != null) {
                        Image(
                            bitmap = profileBitmap!!.asImageBitmap(),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(108.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile_picture),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(108.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = firstName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = TextColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "View and manage your account",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextColor.copy(alpha = 0.7f)
                )
            }
        }

        // ---------- Bottom Section with Profile Items ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .offset(y = 24.dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFFE1F0F7))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 40.dp,
                        bottom = 32.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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
                                .background(ButtonBlue.copy(alpha = 0.95f)),
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

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = TextColor,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
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
