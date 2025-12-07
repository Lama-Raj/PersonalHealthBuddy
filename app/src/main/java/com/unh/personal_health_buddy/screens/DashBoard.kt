package com.unh.personal_health_buddy.screens

import TempProfileStorage
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.R
import com.unh.personal_health_buddy.database.UserDataCache
import com.unh.personal_health_buddy.ui.theme.BloodOrange
import com.unh.personal_health_buddy.ui.theme.BmiPink
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.ChatGreen
import com.unh.personal_health_buddy.ui.theme.EmergencyRed
import com.unh.personal_health_buddy.ui.theme.MediumGray
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import com.unh.personal_health_buddy.ui.theme.PrimaryDarkBlue
import com.unh.personal_health_buddy.ui.theme.ReportsCyan
import com.unh.personal_health_buddy.ui.theme.White
import java.util.Calendar
import android.graphics.Bitmap
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * A simple data class to hold info for our feature cards.
 */
data class Feature(
    val text: String,
    @DrawableRes val imageId: Int
)

// Time-based greeting
private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good Morning,"
        in 12..16 -> "Good Afternoon,"
        in 17..20 -> "Good Evening,"
        else -> "Welcome back,"
    }
}

// Function for date
private fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    // -------- LOAD NAME + PROFILE FROM CACHE / TEMP STORAGE --------
    var firstName by remember { mutableStateOf(UserDataCache.user?.firstname ?: "User") }
    var profileBitmap by remember { mutableStateOf<Bitmap?>(UserDataCache.profileBitmap) }

    val greeting by remember { mutableStateOf(getGreeting()) }
    val currentDate by remember { mutableStateOf(getCurrentDate()) }


    // When UserDataCache finishes loading, update UI
    LaunchedEffect(UserDataCache.isDataLoaded) {
        if (UserDataCache.isDataLoaded) {
            firstName = UserDataCache.user?.firstname ?: "User"
            profileBitmap = UserDataCache.profileBitmap

            // If a new photo was just taken, use that
            TempProfileStorage.tempProfileBitmap?.let {
                profileBitmap = it
            }
        }
    }

    // Also react whenever a new temp profile photo is set
    LaunchedEffect(TempProfileStorage.tempProfileBitmap) {
        TempProfileStorage.tempProfileBitmap?.let {
            profileBitmap = it
        }
    }

    // Features
    val feature1 = Feature("BMI", R.drawable.bmical)
    val feature2 = Feature("Blood Group", R.drawable.blood)
    val feature3 = Feature("Medications", R.drawable.med)
    val feature4 = Feature("Emergency\nContacts", R.drawable.call)
    val feature5 = Feature("Chat\nWith AI", R.drawable.chatai)

    // Gradient background
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            White,
            ButtonBlue.copy(alpha = 0.12f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // --- TOP SECTION ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        // ------- DYNAMIC PROFILE IMAGE (from DB / cache) -------
                        if (profileBitmap != null) {
                            Image(
                                bitmap = profileBitmap!!.asImageBitmap(),
                                contentDescription = "User Profile Picture",
                                modifier = Modifier
                                    .size(120.dp)
                                    .border(4.dp, PrimaryDarkBlue, CircleShape)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "User Profile Picture",
                                modifier = Modifier
                                    .size(120.dp)
                                    .border(4.dp, PrimaryDarkBlue, CircleShape)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = greeting,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryDarkBlue
                            )
                            // ------- DYNAMIC NAME -------
                            Text(
                                text = firstName,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarkBlue
                            )
                            Text(
                                text = "How is it going today?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PrimaryDarkBlue.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(PrimaryDarkBlue.copy(alpha = 0.09f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentDate,
                            fontSize = 12.sp,
                            color = PrimaryDarkBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.doctor),
                    contentDescription = "Doctor Illustration",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-12).dp, y = 8.dp)
                        .size(150.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // --- BOTTOM WHITE CARD SECTION ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 24.dp,
                                bottom = 16.dp
                            )
                        ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Quick Actions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryDarkBlue
                        )
                        Text(
                            text = "Access your key health tools in one tap.",
                            fontSize = 13.sp,
                            color = MediumGray
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StandardFeatureCard(
                                feature = feature1,
                                onClick = { navController.navigate("bmi_screen") },
                                backgroundColor = BmiPink,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                            StandardFeatureCard(
                                feature = feature2,
                                onClick = { navController.navigate("blood_group_screen") },
                                backgroundColor = BloodOrange,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                            StandardFeatureCard(
                                feature = feature3,
                                onClick = { navController.navigate("medicates_screen") },
                                backgroundColor = ReportsCyan,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StandardFeatureCard(
                                feature = feature4,
                                onClick = { navController.navigate("emergency_screen") },
                                backgroundColor = EmergencyRed,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                            LargeFeatureCard(
                                feature = feature5,
                                onClick = { navController.navigate("chat_ai_screen") },
                                backgroundColor = ChatGreen,
                                modifier = Modifier
                                    .weight(2f)
                                    .aspectRatio(2f)
                            )
                        }

                        Text(
                            text = "Health Articles",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryDarkBlue
                        )
                    }
                }
            }
        }
    }
}

/**
 * Standard small cards (BMI, Blood Group, Medications, Emergency Contacts)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardFeatureCard(
    feature: Feature,
    onClick: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = feature.imageId),
                contentDescription = "${feature.text} Illustration",
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = feature.text,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                lineHeight = 17.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Large "Chat with AI" hero card.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeFeatureCard(
    feature: Feature,
    onClick: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(PrimaryDarkBlue, ButtonBlue)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = feature.text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "Ask questions and get quick guidance.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Image(
                    painter = painterResource(id = feature.imageId),
                    contentDescription = "${feature.text} Illustration",
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    PersonalHealthBuddyTheme {
        val navController = rememberNavController()
        HomeScreen(navController = navController)
    }
}
