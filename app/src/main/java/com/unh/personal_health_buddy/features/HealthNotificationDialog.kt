package com.unh.personal_health_buddy.features

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Data class for notifications
data class HealthNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val icon: ImageVector,
    val priority: NotificationPriority
)

enum class NotificationType {
    BMI_WARNING,
    BMI_REMINDER,
    BLOOD_DONATION,
    PRESCRIPTION_REMINDER,
    GENERAL_INFO
}

enum class NotificationPriority {
    HIGH,    // Red/Orange - Urgent
    MEDIUM,  // Yellow - Important
    LOW      // Blue/Green - Informational
}

// Main Notification Display (At Bottom of Screen)
@Composable
fun HealthNotificationDialog(
    notifications: List<HealthNotification>,
    onDismiss: () -> Unit,
    onClearNotification: (String) -> Unit
) {
    // Display notifications as floating cards at the BOTTOM of screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            notifications.forEach { notification ->
                NotificationCard(
                    notification = notification,
                    onDismiss = { onClearNotification(notification.id) }
                )
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: HealthNotification,
    onDismiss: () -> Unit
) {
    val backgroundColor = when (notification.priority) {
        NotificationPriority.HIGH -> Color(0xFFFFEBEE)
        NotificationPriority.MEDIUM -> Color(0xFFFFF3E0)
        NotificationPriority.LOW -> Color(0xFFE0F2F1)
    }

    val iconColor = when (notification.priority) {
        NotificationPriority.HIGH -> Color(0xFFD32F2F)
        NotificationPriority.MEDIUM -> Color(0xFFF57C00)
        NotificationPriority.LOW -> Color(0xFF00897B)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = Color(0xFF616161),
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Dismiss button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Dismiss",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Helper function to generate notifications based on health data
fun generateHealthNotifications(
    bmi: Float?,
    bmiCategory: String,
    bloodType: String?,
    hasPrescriptions: Boolean,
    lastBmiCheckDays: Int = 0
): List<HealthNotification> {
    val notifications = mutableListOf<HealthNotification>()

    // BMI Warnings - Check if BOTH bmi and category exist
    if (bmi != null && bmiCategory.isNotBlank()) {
        when (bmiCategory) {
            "Underweight" -> {
                notifications.add(
                    HealthNotification(
                        id = "bmi_underweight",
                        title = "BMI Alert: Underweight",
                        message = "Your BMI is ${String.format("%.1f", bmi)}. You may be at risk of nutritional deficiencies. Consider consulting a healthcare provider.",
                        type = NotificationType.BMI_WARNING,
                        icon = Icons.Default.Warning,
                        priority = NotificationPriority.HIGH
                    )
                )
            }

            "Overweight" -> {
                notifications.add(
                    HealthNotification(
                        id = "bmi_overweight",
                        title = "BMI Alert: Overweight",
                        message = "Your BMI is ${String.format("%.1f", bmi)}. You may be at increased risk of health problems. A healthier diet and exercise are recommended.",
                        type = NotificationType.BMI_WARNING,
                        icon = Icons.Default.FitnessCenter,
                        priority = NotificationPriority.MEDIUM
                    )
                )
            }

            "Obese" -> {
                notifications.add(
                    HealthNotification(
                        id = "bmi_obese",
                        title = "BMI Alert: Obesity",
                        message = "Your BMI is ${String.format("%.1f", bmi)}. You are at high risk for health conditions. Please consult a healthcare provider for guidance.",
                        type = NotificationType.BMI_WARNING,
                        icon = Icons.Default.LocalHospital,
                        priority = NotificationPriority.HIGH
                    )
                )
            }

            "Healthy" -> {
                notifications.add(
                    HealthNotification(
                        id = "bmi_healthy",
                        title = "Great Job!",
                        message = "Your BMI is ${String.format("%.1f", bmi)} - you're in a healthy weight range! Keep up the good work with a balanced diet and regular exercise.",
                        type = NotificationType.BMI_WARNING,
                        icon = Icons.Default.Check,
                        priority = NotificationPriority.LOW
                    )
                )
            }
        }
    }

    // BMI Check Reminder
    if (lastBmiCheckDays > 30) {
        notifications.add(
            HealthNotification(
                id = "bmi_reminder",
                title = "BMI Check Reminder",
                message = "It's been a while since your last BMI check. Consider checking your BMI to stay on top of your health!",
                type = NotificationType.BMI_REMINDER,
                icon = Icons.Default.Schedule,
                priority = NotificationPriority.LOW
            )
        )
    }

    // Blood Donation Opportunity
    bloodType?.let { type ->
        val isUniversalDonor = type == "O-"
        val isUniversalReceiver = type == "AB+"

        if (isUniversalDonor) {
            notifications.add(
                HealthNotification(
                    id = "blood_universal_donor",
                    title = "You're a Universal Donor!",
                    message = "Your blood type $type is the universal donor. Your donation can save lives for anyone! Consider donating blood regularly.",
                    type = NotificationType.BLOOD_DONATION,
                    icon = Icons.Default.Bloodtype,
                    priority = NotificationPriority.MEDIUM
                )
            )
        } else if (isUniversalReceiver) {
            notifications.add(
                HealthNotification(
                    id = "blood_universal_receiver",
                    title = "Universal Receiver",
                    message = "Your blood type $type can receive from anyone! You're fortunate to have many potential donors.",
                    type = NotificationType.BLOOD_DONATION,
                    icon = Icons.Default.Bloodtype,
                    priority = NotificationPriority.LOW
                )
            )
        } else {
            notifications.add(
                HealthNotification(
                    id = "blood_donation_reminder",
                    title = "Blood Donation Reminder",
                    message = "Your blood type is $type. Consider donating blood to help those in need. Every donation counts!",
                    type = NotificationType.BLOOD_DONATION,
                    icon = Icons.Default.Favorite,
                    priority = NotificationPriority.LOW
                )
            )
        }
    }

    // Prescription Reminder
    if (hasPrescriptions) {
        notifications.add(
            HealthNotification(
                id = "prescription_reminder",
                title = "Medication Reminder",
                message = "Don't forget to take your medications as prescribed. Check your prescription list for details.",
                type = NotificationType.PRESCRIPTION_REMINDER,
                icon = Icons.Default.Medication,
                priority = NotificationPriority.HIGH
            )
        )
    }

    // Extra general nudges to increase number of notifications
    notifications.add(
        HealthNotification(
            id = "nudge_bmi",
            title = "Check Your BMI",
            message = "Curious about your health? Open the BMI tool and see how you're doing today.",
            type = NotificationType.BMI_REMINDER,
            icon = Icons.Default.MonitorHeart,
            priority = NotificationPriority.LOW
        )
    )

    if (hasPrescriptions) {
        notifications.add(
            HealthNotification(
                id = "nudge_meds",
                title = "Medication Check-in",
                message = "Have you taken all of your medications for today? Tap the Medication tab to review.",
                type = NotificationType.PRESCRIPTION_REMINDER,
                icon = Icons.Default.Medication,
                priority = NotificationPriority.MEDIUM
            )
        )
    }

    notifications.add(
        HealthNotification(
            id = "nudge_walk",
            title = "Time for a Quick Walk",
            message = "Even a 10-minute walk can boost your mood and health. Consider stretching your legs!",
            type = NotificationType.GENERAL_INFO,
            icon = Icons.Default.DirectionsWalk,
            priority = NotificationPriority.LOW
        )
    )

    return notifications
}

// Auto-show notification on screen load - Simplified
@Composable
fun AutoShowNotificationEffect(
    notifications: List<HealthNotification>,
    onShow: () -> Unit
) {
    LaunchedEffect(notifications) {
        if (notifications.isNotEmpty()) {
            delay(1000) // Wait 1 second after screen loads
            onShow()
        }
    }
}
