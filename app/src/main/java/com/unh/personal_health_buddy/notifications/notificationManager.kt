package com.unh.personal_health_buddy.notifications

import NotificationDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf

// -------------------- InAppNotificationManager --------------------


import androidx.compose.runtime.mutableStateListOf

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.*

// ---------------- Notification Manager ----------------
object InAppNotificationManager {
    data class Notification(
        val id: Int,
        val title: String,
        val message: String,
        val timestamp: Long = System.currentTimeMillis(),
        val isRead: Boolean = false
    )

    private val _notifications = mutableStateListOf<Notification>()
    val notifications: SnapshotStateList<Notification> get() = _notifications

    val unreadCount: Int
        get() = _notifications.count { !it.isRead }

    fun addNotification(title: String, message: String) {
        _notifications.add(
            0,
            Notification(
                id = _notifications.size,
                title = title,
                message = message
            )
        )
    }

    fun markAllAsRead() {
        _notifications.replaceAll { it.copy(isRead = true) }
    }

    fun markAsRead(notification: Notification) {
        val index = _notifications.indexOfFirst { it.id == notification.id }
        if (index != -1) {
            _notifications[index] = _notifications[index].copy(isRead = true)
        }
    }

    fun delete(notification: Notification) {
        _notifications.remove(notification)
    }

    fun clearAll() {
        _notifications.clear()
    }
}

// ---------------- Item ----------------
@Composable
fun NotificationItem(
    notification: InAppNotificationManager.Notification,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val backgroundColor = if (notification.isRead) Color.White else Color(0xFFE0F7FA)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00796B))
                        .align(Alignment.Top)
                )
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00796B)
                )
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatTimestamp(notification.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ---------------- Timestamp ----------------
private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

// ---------------- Usage Example ----------------
@Composable
fun NotificationDemo() {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            InAppNotificationManager.addNotification(
                title = "New Message",
                message = "Hello! This is a test notification."
            )
            showDialog = true
        }) {
            Text("Show Notifications")
        }

        if (showDialog) {
            NotificationDialog(onDismiss = { showDialog = false })
        }
    }
}

