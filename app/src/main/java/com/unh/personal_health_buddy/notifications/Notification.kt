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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.unh.personal_health_buddy.Authentication.FirestoreHelper
import com.unh.personal_health_buddy.features.HealthNotification
import com.unh.personal_health_buddy.features.NotificationPriority
import com.unh.personal_health_buddy.features.NotificationType
import com.unh.personal_health_buddy.features.generateHealthNotifications
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

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

    // IDs of notifications the user has cleared – they should NOT appear again
    var dismissedIds by rememberSaveable { mutableStateOf(setOf<String>()) }

    // Notifications that are currently visible on the page (gradually revealed)
    var visibleNotifications by remember { mutableStateOf<List<HealthNotification>>(emptyList()) }

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
    val allNotifications = remember(generatedNotifications, InAppNotificationManager.notifications.toList()) {
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

        sessionNotifications + generatedNotifications
    }

    // Filter out notifications that were cleared by the user
    val remainingNotifications = allNotifications.filter { it.id !in dismissedIds }

    // Gradually reveal notifications one by one whenever remainingNotifications changes
    LaunchedEffect(remainingNotifications) {
        // If nothing remaining, clear visible and stop
        if (remainingNotifications.isEmpty()) {
            visibleNotifications = emptyList()
            return@LaunchedEffect
        }

        // Reset visible list and show items one by one
        visibleNotifications = emptyList()
        for (notification in remainingNotifications) {
            visibleNotifications = visibleNotifications + notification
            delay(1000) // 10 second between each → feels "slow but not annoying"
        }
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
                    // - Marks currently visible as dismissed (they won’t show again)
                    // - Clears InApp notifications as well
                    IconButton(
                        onClick = {
                            // Mark all currently visible notifications as dismissed
                            dismissedIds = dismissedIds + visibleNotifications.map { it.id }
                            // Clear visible list
                            visibleNotifications = emptyList()
                            // Clear in-app session notifications
                            InAppNotificationManager.notifications.clear()
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
            when {
                // If still loading AND nothing visible yet, just show empty state (no spinner)
                isLoading && visibleNotifications.isEmpty() && remainingNotifications.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        EmptyState(color = activeColor)
                    }
                }

                // After load: nothing remaining at all → No notifications
                !isLoading && remainingNotifications.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        EmptyState(color = activeColor)
                    }
                }

                else -> {
                    // Show ONLY notifications that have been gradually revealed so far
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
    // Bare list look: default surface, no card
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
            thickness = DividerDefaults.Thickness, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
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
