package com.unh.personal_health_buddy.features

import android.util.Log
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.unh.personal_health_buddy.Authentication.FirestoreHelper
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

// Static data from your theme version

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

    // blue primary color
    val primaryBlue = Color(0xFF1877F2)

    // dynamic user blood type from Firestore (default "Unknown")
    var userBloodType by remember { mutableStateOf("N/A") }
    var isLoading by remember { mutableStateOf(true) }

    // track missing blood group
    var isBloodGroupMissing by remember { mutableStateOf(false) }
    var showMissingDialog by remember { mutableStateOf(false) }

    // Only used for this screen: optional pop notification for universal donor
    var showNotifications by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf<List<HealthNotification>>(emptyList()) }

    // Load blood group from Firestore
    LaunchedEffect(Unit) {
        try {
            val healthInfo = withContext(Dispatchers.IO) {
                FirestoreHelper.getHealthInformation()
            }

            val bloodGroup = healthInfo?.bloodGroup

            if (bloodGroup.isNullOrBlank()) {
                // blood group not set in profile
                isBloodGroupMissing = true
                userBloodType = "N/A"
                showMissingDialog = true
            } else {
                userBloodType = bloodGroup
            }
        } catch (e: Exception) {
            Log.e("BloodGroupScreen", "Error loading blood group: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // Generate health notifications based on blood type (only if present)
    // - not loading
    // - blood group is not missing
    // - user is universal donor (O-)
    LaunchedEffect(userBloodType, isLoading, isBloodGroupMissing) {
        if (!isLoading && !isBloodGroupMissing) {
            if (userBloodType == "O-") {
                val type = userBloodType
                notifications = listOf(
                    HealthNotification(
                        id = "blood_universal_donor",
                        title = "You're a Universal Donor!",
                        message = "Your blood type $type is the universal donor. Your donation can save lives for anyone! Consider donating blood regularly.",
                        type = NotificationType.BLOOD_DONATION,
                        icon = Icons.Default.Bloodtype,
                        priority = NotificationPriority.MEDIUM
                    )
                )
                delay(2000)
                showNotifications = true
            } else {
                // No extra pop notification for other blood types
                notifications = emptyList()
                showNotifications = false
            }
        }
    }

    // Light blue → white gradient (same vibe as other screens)
    val backgroundGradient = Brush.verticalGradient(
        listOf(Color(0xFFE8F1FF), Color.White)
    )

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
                            "Blood Group Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Left,
                            color = primaryBlue
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = primaryBlue
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryBlue)
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {

                    // circle logo
                    SimpleWhiteCircleLogoWithBorder(
                        bloodType = userBloodType,
                        color = primaryBlue
                    )

                    // dynamic table for donation info
                    DonationReceiveTable(
                        primaryTeal = primaryBlue,
                        userBloodType = userBloodType
                    )

                    // list of facts
                    FactsFormView(primaryTeal = primaryBlue)
                }
            }
        }

        // popup dialog when blood group is missing
        if (showMissingDialog) {
            AlertDialog(
                onDismissRequest = { showMissingDialog = false },
                title = {
                    Text(
                        text = "Blood Group Missing",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Text(
                        text = "Please update your blood group in your profile to get the full benefit from this page."
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showMissingDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        // Pop notification ONLY for universal donor case
        if (showNotifications && notifications.isNotEmpty()) {
            HealthNotificationDialog(
                notifications = notifications,
                onDismiss = { showNotifications = false },
                onClearNotification = { id ->
                    notifications = notifications.filter { it.id != id }
                    if (notifications.isEmpty()) {
                        showNotifications = false
                    }
                }
            )
        }
    }
}

// circle logo view
@Composable
fun SimpleWhiteCircleLogoWithBorder(bloodType: String, color: Color) {

    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(4.dp, color.copy(alpha = 0.45f), CircleShape),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Bloodtype,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(45.dp)
            )

            Text(
                text = bloodType,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

// dynamic table view
@Composable
fun DonationReceiveTable(primaryTeal: Color, userBloodType: String) {

    val info = bloodCompatibility[userBloodType]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
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
                            .background(Color(0xFFF3F6FF), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = info?.donateTo?.joinToString(", ") ?: "Unknown",
                            fontSize = 14.sp,
                            color = Color(0xFF0D47A1)
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
                            .background(Color(0xFFF3F6FF), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = info?.receiveFrom?.joinToString(", ") ?: "Unknown",
                            fontSize = 14.sp,
                            color = Color(0xFF0D47A1)
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
                        color = Color(0xFF1A237E),
                        lineHeight = 20.sp
                    )
                }

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
