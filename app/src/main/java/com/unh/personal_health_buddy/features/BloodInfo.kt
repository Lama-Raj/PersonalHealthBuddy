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

// fact list for bottom view
private val bloodFacts = listOf(
    "There are eight major human blood types.",
    "O- is the universal donor type.",
    "AB+ is the universal receiver type.",
    "Your blood type is inherited from your parents.",
    "One blood donation can save up to three lives."
)

// data for compatibility matching
data class BloodInfo(
    val donateTo: List<String>,
    val receiveFrom: List<String>
)

// match table for each blood type
private val bloodCompatibility = mapOf(
    "O-" to BloodInfo(
        donateTo = listOf("Everyone"),
        receiveFrom = listOf("O-")
    ),
    "O+" to BloodInfo(
        donateTo = listOf("O+", "A+", "B+", "AB+"),
        receiveFrom = listOf("O+", "O-")
    ),
    "A-" to BloodInfo(
        donateTo = listOf("A-", "A+", "AB-", "AB+"),
        receiveFrom = listOf("A-", "O-")
    ),
    "A+" to BloodInfo(
        donateTo = listOf("A+", "AB+"),
        receiveFrom = listOf("A+", "A-", "O+", "O-")
    ),
    "B-" to BloodInfo(
        donateTo = listOf("B-", "B+", "AB-", "AB+"),
        receiveFrom = listOf("B-", "O-")
    ),
    "B+" to BloodInfo(
        donateTo = listOf("B+", "AB+"),
        receiveFrom = listOf("B+", "B-", "O+", "O-")
    ),
    "AB-" to BloodInfo(
        donateTo = listOf("AB-", "AB+"),
        receiveFrom = listOf("AB-", "A-", "B-", "O-")
    ),
    "AB+" to BloodInfo(
        donateTo = listOf("AB+"),
        receiveFrom = listOf("Everyone")
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodGroupScreen(navController: NavController) {

    val primaryTeal = Color(0xFF00796B)

    // user blood type (for now, hardcoded)
    val userBloodType = "O+"   // change later when Firestore is ready

    // soft gradient background
    val backgroundGradient = Brush.verticalGradient(
        listOf(Color(0xFFE0F7FA), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient) // blend screen background
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
                            textAlign = TextAlign.Center // center title text
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = primaryTeal // match color theme
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()) // allow scroll on small screens
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {

                // show circle logo
                SimpleWhiteCircleLogoWithBorder(
                    bloodType = userBloodType,
                    color = primaryTeal
                )

                // show dynamic table for donation info
                DonationReceiveTable(
                    primaryTeal = primaryTeal,
                    userBloodType = userBloodType
                )

                // show list of facts
                FactsFormView(primaryTeal)
            }
        }
    }
}

// circle logo view
@Composable
fun SimpleWhiteCircleLogoWithBorder(bloodType: String, color: Color) {

    Box(
        modifier = Modifier
            .size(140.dp)             // circle dimensions
            .clip(CircleShape)        // force circle shape
            .background(Color.White)  // keep inside area white
            .border(4.dp, color.copy(alpha = 0.45f), CircleShape), // soft border glow
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Bloodtype,
                contentDescription = null,
                tint = color,              // icon matches theme
                modifier = Modifier.size(45.dp)
            )

            Text(
                text = bloodType,
                fontSize = 28.sp,          // show big type
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

// dynamic table view
@Composable
fun DonationReceiveTable(primaryTeal: Color, userBloodType: String) {

    // read matching entry based on chosen blood type
    val info = bloodCompatibility[userBloodType]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp)) // soft table box
            .border(1.dp, primaryTeal.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            Text(
                "Blood Donation Info",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = primaryTeal
            )

            // thin line under title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(primaryTeal.copy(alpha = 0.15f))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // left column: donation list
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        "Can Donate To",
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
                            text = info?.donateTo?.joinToString(", ") ?: "Unknown",
                            fontSize = 14.sp,
                            color = Color(0xFF004D40)
                        )
                    }
                }

                // vertical line to separate columns
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(70.dp)
                        .background(primaryTeal.copy(alpha = 0.25f))
                )

                // right column: receive list
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        "Can Receive From",
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
                            text = info?.receiveFrom?.joinToString(", ") ?: "Unknown",
                            fontSize = 14.sp,
                            color = Color(0xFF004D40)
                        )
                    }
                }
            }
        }
    }
}

// fact list below table
@Composable
fun FactsFormView(primaryTeal: Color) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Text(
            "Blood Facts",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = primaryTeal,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        bloodFacts.forEachIndexed { index, fact ->

            Column(modifier = Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Bloodtype,
                        contentDescription = null,
                        tint = primaryTeal.copy(alpha = 0.85f),
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        fact,
                        fontSize = 15.sp,
                        color = Color(0xFF00332B),
                        lineHeight = 20.sp
                    )
                }

                // divider line between fact rows
                if (index != bloodFacts.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(primaryTeal.copy(alpha = 0.15f))
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
