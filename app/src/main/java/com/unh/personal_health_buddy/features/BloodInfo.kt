package com.unh.personal_health_buddy.features

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
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

// Fact list shown in UI
private val bloodFacts = listOf(
    "There are eight major human blood types.",
    "O- is the universal donor type.",
    "AB+ is the universal receiver type.",
    "Your blood type is inherited from your parents.",
    "One blood donation can save up to three lives."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodGroupScreen(navController: NavController) {

    val userBloodType = "O+"                  // Temporary user value
    val primaryTeal = Color(0xFF00796B)       // Main accent color

    // Background gradient for page
    val backgroundGradient = Brush.verticalGradient(
        listOf(Color(0xFFE0F7FA), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)     // Apply gradient
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Blood Group Details",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center             // Center title
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",        // Back button
                                tint = primaryTeal
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent          // No appbar background
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())          // Enable scrolling
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {

                // Show main blood icon & type
                SimpleWhiteCircleLogoWithBorder(
                    bloodType = userBloodType,
                    color = primaryTeal
                )

                // Add donation + receive table here
                DonationReceiveTable(primaryTeal)

                // Show list of facts
                FactsFormView(primaryTeal)
            }
        }
    }
}

@Composable
fun SimpleWhiteCircleLogoWithBorder(bloodType: String, color: Color) {

    Box(
        modifier = Modifier
            .size(140.dp)                             // Size of circle
            .clip(CircleShape)                        // Apply circle shape
            .background(Color.White)                  // Circle background
            .border(4.dp, color.copy(alpha = 0.45f), CircleShape), // Circle border
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Bloodtype,
                contentDescription = null,
                tint = color,                         // Icon color
                modifier = Modifier.size(45.dp)       // Icon size
            )

            Text(
                text = bloodType,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,                     // Blood type size
                color = color
            )
        }
    }
}

@Composable
fun DonationReceiveTable(primaryTeal: Color) {

    // table container
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))   // rounded card
            .border(1.dp, primaryTeal.copy(alpha = 0.25f), RoundedCornerShape(12.dp)) // soft border
            .padding(16.dp)
    ) {

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            // Section title
            Text(
                text = "Blood Donation Info",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = primaryTeal
            )

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(primaryTeal.copy(alpha = 0.15f))
            )

            // Table: side-by-side layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Left column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text = "Can Donate To",
                        fontSize = 15.sp,
                        color = primaryTeal,
                        fontWeight = FontWeight.Medium
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1FDFC), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Placeholder",
                            color = Color(0xFF004D40),
                            fontSize = 14.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(70.dp)
                        .background(primaryTeal.copy(alpha = 0.25f))
                )

                // Right column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text = "Can Receive From",
                        fontSize = 15.sp,
                        color = primaryTeal,
                        fontWeight = FontWeight.Medium
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1FDFC), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Placeholder",
                            color = Color(0xFF004D40),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FactsFormView(primaryTeal: Color) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Text(
            text = "Blood Facts",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = primaryTeal,                     // Section title color
            modifier = Modifier.padding(bottom = 4.dp)
        )

        bloodFacts.forEachIndexed { index, fact ->

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),   // Row spacing
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Bloodtype,
                        contentDescription = null,
                        tint = primaryTeal.copy(alpha = 0.85f), // Icon tint
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = fact,
                        fontSize = 15.sp,
                        color = Color(0xFF00332B),     // Readable text color
                        lineHeight = 20.sp
                    )
                }

                if (index != bloodFacts.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(primaryTeal.copy(alpha = 0.15f)) // Divider line
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BloodGroupScreenPreview() {
    PersonalHealthBuddyTheme {
        BloodGroupScreen(navController = rememberNavController())
    }
}
