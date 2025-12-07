package com.unh.personal_health_buddy.navigation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.navigation.bottomNavItems
import com.unh.personal_health_buddy.notifications.InAppNotificationManager
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.MediumGray

// -------------------- BottomNavBar --------------------


@Composable
fun BottomNavBar(
    currentRoute: String?,
    navController: NavHostController,
    onNotificationClick: () -> Unit
) {
    val notifications = InAppNotificationManager.notifications
    val unreadCount by remember {
        derivedStateOf { notifications.count { !it.isRead } }
    }

    NavigationBar(
        // ADDED THIS MODIFIER to clip the corners
        modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ){
        bottomNavItems.forEach { item ->
            val selected = currentRoute?.startsWith(item.route) == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (item.route == "notifications") {
                        onNotificationClick()
                    } else if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    if (item.route == "notifications" && unreadCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge {
                                    Text(
                                        text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        }
                    } else {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    }
                },
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewBottomNavBar() {
    val navController = rememberNavController()
    BottomNavBar(
        currentRoute = "home",
        navController = navController,
        onNotificationClick = {} // Pass an empty lambda
    )
}
