package com.unh.personal_health_buddy.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.authentication.FirestoreHelper
import com.unh.personal_health_buddy.features.HealthNotification
import com.unh.personal_health_buddy.features.NotificationPriority
import com.unh.personal_health_buddy.features.NotificationType
import com.unh.personal_health_buddy.features.generateHealthNotifications
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Navigation Definitions
sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : NavigationItem("home", Icons.Filled.Home, "Home")
    object Map : NavigationItem("map", Icons.Filled.Place, "Map")
    object Notification : NavigationItem("notification", Icons.Filled.Notifications, "Notification")
    object Profile : NavigationItem("profile", Icons.Filled.Person, "Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
    // STYLING
    val activeColor = Color(0xFF1877F2)

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE7F0FF), // very light blue
            Color(0xFFFFFFFF)  // white
        )
    )

    // Notifications fetched/generated from data
    var generatedNotifications by remember { mutableStateOf<List<HealthNotification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Notifications currently visible on the page
    var visibleNotifications by remember { mutableStateOf<List<HealthNotification>>(emptyList()) }

    // 🔹 Use the global dismissedKeys as reactive state
    val dismissedKeys = NotificationHistory.dismissedKeys

    // Fetch data and generate notifications once
    LaunchedEffect(Unit) {
        try {
            val info = withContext(Dispatchers.IO) { FirestoreHelper.getHealthInformation() }
            val prescriptions = withContext(Dispatchers.IO) { FirestoreHelper.readAllPrescriptions() }

            val hasMeds = prescriptions.isNotEmpty()

            generatedNotifications = generateHealthNotifications(
                bmi = null,
                bmiCategory = "",
                bloodType = info?.bloodGroup,
                hasPrescriptions = hasMeds,
                lastBmiCheckDays = 0
            )
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    // Combine generated notifications with In-App Session notifications
    val allNotifications = remember(
        generatedNotifications,
        InAppNotificationManager.notifications.toList(),
        dismissedKeys
    ) {
        val sessionNotifications = InAppNotificationManager.notifications.map { inApp ->
            HealthNotification(
                id = inApp.id.toString(),          // Int -> String
                title = inApp.title,
                message = inApp.message,
                type = NotificationType.GENERAL_INFO,
                icon = Icons.Default.Notifications,
                priority = NotificationPriority.MEDIUM
            )
        }

        (sessionNotifications + generatedNotifications)
            // 1) Remove anything the user has dismissed (ever) based on content
            .filter {
                val key = NotificationHistory.makeKey(it.title, it.message)
                key !in dismissedKeys
            }
            // 2) Remove duplicates with same content currently in the list
            .distinctBy { NotificationHistory.makeKey(it.title, it.message) }
    }

    // Show all remaining notifications immediately (no delay)
    LaunchedEffect(allNotifications) {
        visibleNotifications = allNotifications
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Clear notifications button:
                    // - Marks current visible as dismissed globally (by content)
                    // - Clears InApp notifications
                    IconButton(
                        onClick = {
                            NotificationHistory.dismissMany(visibleNotifications)
                            visibleNotifications = emptyList()
                            InAppNotificationManager.clearAll()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear notifications",
                            tint = activeColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = activeColor,
                    navigationIconContentColor = activeColor
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            val hasAnythingToShow = visibleNotifications.isNotEmpty()

            when {
                // Nothing to show → empty state
                !hasAnythingToShow -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        EmptyState(color = activeColor)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(visibleNotifications, key = { it.id }) { notification ->
                            NotificationListItem(notification = notification)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationListItem(
    notification: HealthNotification
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = notification.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = secondaryTextColor
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            thickness = DividerDefaults.Thickness,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        )
    }
}

@Composable
private fun EmptyState(color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "No Notifications",
            tint = color.copy(alpha = 0.6f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No Notifications Yet",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You'll be notified here once there's something new.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    PersonalHealthBuddyTheme {
        NotificationScreen(navController = rememberNavController())
    }
}
