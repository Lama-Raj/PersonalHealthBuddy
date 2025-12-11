
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

/*
* Updated the Code using Chat GTP as there was certain errors in the original code.
* Reference Code: YouTube & Chat GPT, Gemini and Copilot
* Reference : Stack Overflow, GitHub, W3Schools
*
* */
object InAppNotificationManager {
    data class Notification(
        val id: String,
        val title: String,
        val message: String,
        val timestamp: Long,
        var isRead: Boolean = false
    )

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    fun add(notification: Notification) {
        _notifications.value = _notifications.value + notification
    }

    fun delete(notification: Notification) {
        _notifications.value = _notifications.value.filterNot { it.id == notification.id }
    }

    fun markAsRead(notification: Notification) {
        _notifications.value = _notifications.value.map {
            if (it.id == notification.id) it.copy(isRead = true) else it
        }
    }

    fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }
}

// ---------------- Dialog ----------------
@Composable
fun NotificationDialog(onDismiss: () -> Unit) {
    val notifications by InAppNotificationManager.notifications.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Notifications") },
        text = {
            if (notifications.isEmpty()) {
                Text("No notifications yet.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    notifications.forEach { n ->
                        NotificationItem(
                            notification = n,
                            onClick = { InAppNotificationManager.markAsRead(n) },
                            onDelete = { InAppNotificationManager.delete(n) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                InAppNotificationManager.markAllAsRead()
                onDismiss()
            }) {
                Text("Mark all as read")
            }
        }
    )
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
            InAppNotificationManager.add(
                InAppNotificationManager.Notification(
                    id = UUID.randomUUID().toString(),
                    title = "New Message",
                    message = "You have a new notification!",
                    timestamp = System.currentTimeMillis()
                )
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
