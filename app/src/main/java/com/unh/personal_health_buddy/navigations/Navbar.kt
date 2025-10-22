package com.unh.personal_health_buddy.navigations

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.MediumGray

// This is a data class. It's like a blueprint for an object.
// It holds the information needed for each item in the bottom navigation bar.
data class BottomNavItem(
    val title: String, // 'val' means this is a read-only variable for the item's name.
    val icon: ImageVector, // This holds the picture for the item.
    val route: String // This is the navigation path for the item.
)

// This is a list. It holds all the 'BottomNavItem' objects we want to show.
val items = listOf(
    BottomNavItem("Home", Icons.Filled.Home, "home"),
    BottomNavItem("Map", Icons.Filled.Place, "map_route"),
    BottomNavItem("Notification", Icons.Filled.Notifications, "notification_route"),
    BottomNavItem("Profile", Icons.Filled.Person, "profile_route")
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    NavigationBar(
        // ADDED THIS MODIFIER to clip the corners
        modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        items.forEach { item ->
            val selected = item.route == currentRoute
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
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

@Preview(showBackground = true) // This annotation tells Android Studio to show a preview of this composable.
@Composable
// This is another composable function, but it's just for the preview.
// It helps you see your UI without running the whole app.
fun BottomNavBarPreview() {
    // We wrap our component in our app's theme to make the preview look correct.
    com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme {
        // Here, we call the 'BottomNavBar' we want to preview.
        BottomNavBar(
            currentRoute = "home",
            onItemClick = {}
        )
    }
}
