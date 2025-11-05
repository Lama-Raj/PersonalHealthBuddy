package com.unh.personal_health_buddy.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme


@Composable
fun BloodGroupScreen(navController: NavController){

    val newGradientStart = Color(0xFFFFFFFF) // White
    val newGradientEnd = Color(0xFFE3F2FD)   // Shining (Light) Blue
    val vibrantGradient = Brush.verticalGradient(colors = listOf(newGradientStart, newGradientEnd))
    val activeColor = Color(0xFF0277BD) // The strong blue from BmiScreen

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(vibrantGradient)
    )

}

@Preview(showBackground = true)
@Composable
fun BloodGroupScreenPreview(){
    PersonalHealthBuddyTheme {
        BloodGroupScreen(navController = rememberNavController())
    }
}