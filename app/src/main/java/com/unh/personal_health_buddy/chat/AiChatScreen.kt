package com.unh.personal_health_buddy.chat

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme

@Composable
fun AiChatScreen(navController: NavController){
    Text("test")

}
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PersonalHealthBuddyTheme {
        AiChatScreen(navController = rememberNavController())
    }
}