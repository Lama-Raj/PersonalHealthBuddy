package com.unh.personal_health_buddy.features

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodGroupScreen(navController: NavController){
    val userBloodType = "AB+" // This Need to be change as per user data entry for now it is static
    val newGradientStart = Color(0xFFFFFFFF) // White
    val newGradientEnd = Color(0xFFE3F2FD)   // Shining (Light) Blue
    val vibrantGradient = Brush.verticalGradient(colors = listOf(newGradientStart, newGradientEnd))
    val activeColor = Color(0xFF800000) // Maroon
    val activeColorDark = Color(0xFF6B0000) // Darker Maroon

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(vibrantGradient)
    )
    {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Your Blood Info",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        // Invisible button to balance the title
                        IconButton(onClick = { }, enabled = false) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.Transparent)
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
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                BloodTypeIcon(bloodType = userBloodType, color = activeColor)

            }
        }
    }

}

@Composable
private fun BloodTypeIcon(bloodType: String, color: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(180.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.1f))
            .border(4.dp, color.copy(alpha = 0.2f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = "Blood Drop",
            tint = color,
            modifier = Modifier.size(140.dp)
        )
        Text(
            text = bloodType,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier
                .clip(CircleShape)
                .background(color.copy(alpha = 0.8f))
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BloodGroupScreenPreview(){
    PersonalHealthBuddyTheme {
        BloodGroupScreen(navController = rememberNavController())
    }
}