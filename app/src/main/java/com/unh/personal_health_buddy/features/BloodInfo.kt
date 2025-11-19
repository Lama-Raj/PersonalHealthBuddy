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

// ---- Facts ----
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

    val userBloodType = "O+"

    val backgroundGradient = Brush.verticalGradient(
        listOf(
            Color(0xFFE0F7FA),
            Color.White
        )
    )

    val primaryTeal = Color(0xFF00796B)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Blood Group Details",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = primaryTeal
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {

                SimpleWhiteCircleLogo(bloodType = userBloodType, color = primaryTeal)

                FormStyleFactsSection(primaryTeal)
            }
        }
    }
}


// ---------------- NEW SIMPLE LOGO ----------------
@Composable
fun SimpleWhiteCircleLogo(bloodType: String, color: Color) {

    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(3.dp, color.copy(alpha = 0.25f), CircleShape),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Bloodtype,
                contentDescription = "Blood Icon",
                tint = color,
                modifier = Modifier.size(45.dp)
            )

            Text(
                text = bloodType,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = color
            )
        }
    }
}


// ---- FORM STYLE FACTS ----
@Composable
fun FormStyleFactsSection(primaryTeal: Color) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFE0F2F1),
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = 1.dp,
                color = primaryTeal.copy(alpha = 0.4f),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "Blood Facts",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = primaryTeal
        )

        bloodFacts.forEach { fact ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bloodtype,
                    contentDescription = null,
                    tint = primaryTeal.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = fact,
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    color = Color(0xFF004D40)
                )
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
