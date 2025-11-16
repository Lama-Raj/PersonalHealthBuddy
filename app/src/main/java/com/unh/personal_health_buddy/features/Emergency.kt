package com.unh.personal_health_buddy.features

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.ui.theme.EmergencyRed
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactScreen(navController: NavController) {

    // --- STYLING ---
    // Using the new light blue gradient
    val newGradientStart = Color(0xFFE3F2FD) // Shining (Light) Blue
    val newGradientEnd = Color(0xFFFFFFFF)   // White
    val vibrantGradient = Brush.verticalGradient(colors = listOf(newGradientStart, newGradientEnd))
    val activeColor = EmergencyRed // Keep using the theme's red for text/icons


    // --- STATE FOR FAB ---
    var isMenuExpanded by remember { mutableStateOf(false) }
    // --- END STATE ---

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(vibrantGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(top = 40.dp),
                    title = {
                        Text(
                            text = "Emergency Contact",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 33.sp,
                            modifier = Modifier.fillMaxWidth(),
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
            // --- ADD FLOATING ACTION BUTTON ---
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Animated menu options
                    AnimatedVisibility(visible = isMenuExpanded) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MiniFabWithText(
                                icon = Icons.Default.Add,
                                text = "Add Contact",
                                onClick = { /* TODO: Handle add */ },
                                activeColor = activeColor,
                            )
                            MiniFabWithText(
                                icon = Icons.Default.Delete,
                                text = "Delete Contact",
                                onClick = { /* TODO: Handle delete */ },
                                activeColor = activeColor,
                            )
                        }
                    }

                    // Main FAB
                    FloatingActionButton(
                        onClick = { isMenuExpanded = !isMenuExpanded },
                        containerColor = activeColor,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Contact"
                        )
                    }
                }
            },
            // --- END FAB ---
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp), // Add padding for content
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Content
            }
        }
    }
}

// --- NEW COMPOSABLE FOR MINI FAB ---
@Composable
private fun MiniFabWithText(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    activeColor: Color, // Use the theme color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Text Label
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = activeColor, // Use theme color
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )

        // Mini FAB
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = Color.White,
            contentColor = activeColor // Use theme color
        ) {
            Icon(imageVector = icon, contentDescription = text)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencyContactScreenPreview() {
    PersonalHealthBuddyTheme {
        EmergencyContactScreen(navController = rememberNavController())
    }
}