package com.unh.personal_health_buddy.features

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme


@Composable
fun BmiScreen(navController: NavController) {


}

@Preview(showBackground = true)
@Composable
fun BmiScreenPreview() {
    PersonalHealthBuddyTheme {
        BmiScreen(navController = rememberNavController())
    }
}

