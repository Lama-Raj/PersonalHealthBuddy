package com.unh.personal_health_buddy.screens

import TempProfileStorage
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.unh.personal_health_buddy.ui.theme.ChatGreen
import com.unh.personal_health_buddy.ui.theme.EmergencyRed
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import com.unh.personal_health_buddy.ui.theme.ReportsCyan
import com.unh.personal_health_buddy.ui.theme.White
import java.util.Calendar   // <-- added for greeting

data class Feature(
    val text: String,
    @DrawableRes val imageId: Int
)

// Helper function for time-based greeting
fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good Morning,"
        in 12..16 -> "Good Afternoon,"
        in 17..20 -> "Good Evening,"
        else -> "Welcome,"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
) {
    // Use cached data instead of fetching from Firestore
    var firstName by remember { mutableStateOf(UserDataCache.user?.firstname ?: "User") }
    var profileBitmap by remember { mutableStateOf(UserDataCache.profileBitmap) }

    val greeting by remember { mutableStateOf(getGreeting()) }

    // Update from cache when it's loaded
    LaunchedEffect(UserDataCache.isDataLoaded) {
        if (UserDataCache.isDataLoaded) {
            firstName = UserDataCache.user?.firstname ?: "User"
            profileBitmap = UserDataCache.profileBitmap

            // Check temp storage for newly taken photos
            TempProfileStorage.tempProfileBitmap?.let {
                profileBitmap = it
            }
        }
    }

    // Update when new photo is taken
    LaunchedEffect(TempProfileStorage.tempProfileBitmap) {
        TempProfileStorage.tempProfileBitmap?.let {
            profileBitmap = it
        }
    }

    // Features for cards
    val feature1 = Feature("BMI\nStatus", R.drawable.bmical)
    val feature2 = Feature("Blood Group\nInfo", R.drawable.blood)
    val feature3 = Feature("Medicates", R.drawable.med)
    val feature4 = Feature("Emergency\nContact", R.drawable.call)
    val feature5 = Feature("Chat\nWith AI", R.drawable.chatai)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
    ) {
        // ---------- TOP USER SECTION ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 40.dp, vertical = 70.dp)

            ) {
                if (profileBitmap != null) {
                    Image(
                        bitmap = profileBitmap!!.asImageBitmap(),
                        contentDescription = "User Profile Picture",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "User Profile Picture",
                        modifier = Modifier
                            .size(90.dp)
                            .padding(top = 32.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    greeting,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    firstName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    "How is it going today?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Image(
                painter = painterResource(id = R.drawable.doctor),
                contentDescription = "Doctor Illustration",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-20).dp, y = 90.dp)
                    .size(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- BOTTOM WHITE AREA WITH CARDS ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .offset(y = (30).dp)
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color.Magenta)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 60.dp,
                        bottom = 60.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
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
                        onClick = { navController.navigate("emergency-contacts") },
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
            }
        }
    }
}

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
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
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
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = feature.text,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                lineHeight = 17.sp,
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = feature.imageId),
                contentDescription = "${feature.text} Illustration",
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Fit
            )
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