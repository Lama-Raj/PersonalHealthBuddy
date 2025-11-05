package com.unh.personal_health_buddy.features

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

// Added static data for now le
private val bloodFacts = listOf(
    "There are eight different common blood types (A+, A-, B+, B-, AB+, AB-, O+, O-).",
    "Blood type O- is the 'universal donor' and can be given to any blood type.",
    "Blood type AB+ is the 'universal recipient' and can receive blood from any type.",
    "Your blood type is inherited from your parents.",
    "A single blood donation can save up to three lives."
)
// --- END STATIC DATA ---


@OptIn(ExperimentalMaterial3Api::class) // Got from auto import
@Composable
fun BloodGroupScreen(navController: NavController){

    // --- ADD STATE ---
    // Hardcoded as for reference image. This will later be changed and fetched from the database.
    val userBloodType = "AB+"
    // --- END ---

    // --- STYLING AND LAYOUT SHELL ---
    // Using the new maroon color theme
    val newGradientStart = Color(0xFFFFF0F0) // Very Light Maroon/Pink
    val newGradientEnd = Color(0xFFFFFFFF)   // White
    val vibrantGradient = Brush.verticalGradient(colors = listOf(newGradientStart, newGradientEnd))
    val activeColor = Color(0xFF800000) // Maroon
    val activeColorDark = Color(0xFF6B0000) // Darker Maroon

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(vibrantGradient)
    ) {
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
                        titleContentColor = activeColorDark,
                        navigationIconContentColor = activeColorDark
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    // --- MODIFIERS FOR COLUMN ---
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
                // --- END MODIFIERS ---
            ) {

                BloodTypeIcon(bloodType = userBloodType, color = activeColor)

                // --- Blood Facts Card ---
                BloodFactsCard(facts = bloodFacts, color = activeColor)

            }
        }
    }
}


@Composable
private fun BloodTypeIcon(bloodType: String, color: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
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

@Composable
private fun BloodFactsCard(facts: List<String>, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        // --- CHANGED ---
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3A6EF)),
        elevation = CardDefaults.cardElevation(2.dp) // Added a subtle shadow
        // --- END CHANGED ---
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp), // Added more padding
            verticalArrangement = Arrangement.spacedBy(16.dp) // Added more spacing
        ) {
            Text(
                text = "Blood Facts",
                color = Color.Black.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            facts.forEach { fact ->
                FactRow(fact = fact, color = color)
            }
        }
    }
}

@Composable
private fun FactRow(fact: String, color: Color) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp) // Added more spacing
    ) {
        Icon(
            imageVector = Icons.Default.WaterDrop, // Use the blood drop icon
            contentDescription = null,
            tint = color.copy(alpha = 0.7f),
            modifier = Modifier
                .size(16.dp)
                .padding(top = 4.dp) // Align icon with first line of text
        )
        Text(
            text = fact,
            color = Color.Black.copy(alpha = 0.7f),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}
// --- END COMPOSABLE ---

@Preview(showBackground = true)
@Composable
fun BloodGroupScreenPreview(){
    PersonalHealthBuddyTheme {
        BloodGroupScreen(navController = rememberNavController())
    }
}