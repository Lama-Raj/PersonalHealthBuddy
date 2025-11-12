package com.unh.personal_health_buddy.features

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme


@Composable
fun MedicateScreen(navController: NavController){
    Text (text = "Test Text By Raj")

}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    PersonalHealthBuddyTheme {
        MedicateScreen(navController = rememberNavController())
    }
}