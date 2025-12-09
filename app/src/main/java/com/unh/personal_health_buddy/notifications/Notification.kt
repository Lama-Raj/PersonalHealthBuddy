package com.unh.personal_health_buddy.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.unh.personal_health_buddy.Authentication.FirestoreHelper
import com.unh.personal_health_buddy.features.HealthNotification
import com.unh.personal_health_buddy.features.NotificationCard
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
    // blue for primary accents
    val activeColor = Color(0xFF1877F2)

    // Soft light-blue to white gradient background
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE7F0FF), // very light blue
            Color(0xFFFFFFFF)  // white
        )
    )

    // Persistent notifications fetched from Firestore data
    var generatedNotifications by remember { mutableStateOf<List<HealthNotification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch data and generate notifications
    LaunchedEffect(Unit) {
        try {
            val info = withContext(Dispatchers.IO) { FirestoreHelper.getHealthInformation() }
            val prescriptions = withContext(Dispatchers.IO) { FirestoreHelper.readAllPrescriptions() }

            val hasMeds = prescriptions.isNotEmpty()

            // Always generate notifications, even if health info is null.
            // generateHealthNotifications will also add a random “nudge” notification.
            generatedNotifications = generateHealthNotifications(
                bmi = null,               // BMI not persistent in HealthInformation yet
                bmiCategory = "",
                bloodType = info?.bloodGroup,
                hasPrescriptions = hasMeds,
                lastBmiCheckDays = 0      // keep consistent with other calls
            )
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    // Combine generated notifications with In-App Session Notifications
    // Since InAppNotificationManager is in the same package, no import needed if package matches.
    val allNotifications = remember(generatedNotifications, InAppNotificationManager.notifications.toList()) {
        // Map InAppNotification (Session) to HealthNotification (Display)
        val sessionNotifications = InAppNotificationManager.notifications.map { inApp ->
            HealthNotification(
                id = inApp.id.toString(), 
                title = inApp.title,
                message = inApp.message,
                type = NotificationType.GENERAL_INFO,
                icon = Icons.Default.Notifications,
                priority = NotificationPriority.MEDIUM
            )
        }

        // Combine: Session notifications first, then generated ones
        sessionNotifications + generatedNotifications
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
                    // Invisible button to balance the title
                    IconButton(onClick = { }, enabled = false) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.Transparent
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
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = activeColor
                    )
                }

                allNotifications.isEmpty() -> {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(allNotifications) { notification ->
                            NotificationCard(
                                notification = notification,
                                onDismiss = {
                                    // If it's a session notification, remove from manager
                                    if (InAppNotificationManager.notifications.any { it.id.toString() == notification.id }) {
                                        InAppNotificationManager.notifications.removeIf {
                                            it.id.toString() == notification.id
                                        }
                                    }
                                    // Generated notifications are persistent based on data state
                                }
                            )
                        }
                    }
                }
            }
        }
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
