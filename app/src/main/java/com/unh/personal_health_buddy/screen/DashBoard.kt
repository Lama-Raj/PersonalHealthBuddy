package com.unh.personal_health_buddy.screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

/**
 * A simple data class to hold info for our feature cards.
 *
 * IMPORTANT: Replace '''R.drawable.profile''' with your actual icon resource IDs.
 * (e.g., R.drawable.bmi_icon, R.drawable.blood_icon, etc.)
 */
data class Feature(
    val text: String,
    @DrawableRes val imageId: Int // This ensures we provide a valid drawable resource ID.
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

@OptIn(ExperimentalMaterial3Api::class) // We need this to use the Material 3 Card.
@Composable // This annotation marks the function as a piece of UI.
fun DashboardScreen(navController: NavController){

    val feature1 = Feature("BMI", R.drawable.bmical) // Pink
    val feature2 = Feature("Blood Group", R.drawable.blood)      // Orange
    val feature3 = Feature("Medications", R.drawable.med)     // Blue
    val feature4 = Feature("Emergency\nContacts", R.drawable.call)    // Red
    val feature5 = Feature("Chat\nWith AI", R.drawable.chatai) // Green

    val greeting = getGreeting()

    // Gradient background using brighter colors
    // White at the top → soft tinted blue towards the bottom.
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            White,
            ButtonBlue.copy(alpha = 0.12f)
        )
    )

    // --- NEW ROBUST LAYOUT ---
    // This Column is the new root. It divides the screen into two weighted sections,
    // which is much more stable than calculating offsets from screen height.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // --- TOP SECTION (about 40% of screen height) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f) // This makes the box take up ~40% of the parent Column's height.
            ) {
                // The user info is aligned to the top-start of this Box.
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(horizontal = 24.dp, vertical = 32.dp), // Simple padding is more reliable.
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // BIG profile on the left + greeting texts on the right
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "User Profile Picture",
                            modifier = Modifier
                                .size(120.dp)        // <-- Bigger profile image
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Time-based greeting
                            Text(
                                text = greeting,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryDarkBlue
                            )
                            Text(
                                text = "User",
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

                    // A small pill chip showing context (placed under the row)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(PrimaryDarkBlue.copy(alpha = 0.06f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Daily overview",
                            fontSize = 12.sp,
                            color = PrimaryDarkBlue
                        )
                    }
                }

                // The doctor image is aligned to the BOTTOM of this Box.
                // This is the key to making its position relative to the white card below.
                Image(
                    painter = painterResource(id = R.drawable.doctor),
                    contentDescription = "Doctor Illustration",
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Align to the bottom-right of the parent Box.
                        // A positive 'y' offset makes the image "hang over" into the section below.
                        .offset(x = (-12).dp, y = 8.dp)
                        .size(150.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // --- BOTTOM WHITE CARD SECTION (remaining screen height) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f) // This takes the remaining height.
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(Color.White)
            ) {
                // We use a Column to hold the header + two rows of cards.
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        // This padding is crucial:
                        // - top: space for the section header
                        // - start/end/bottom: space on the sides and bottom.
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
                    // Section header for this dashboard area
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

                    // These nested Columns/Rows hold the two rows of cards.
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // This is the TOP row of cards
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

                        // This is the BOTTOM row of cards
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
                    }
                }
            }
        }
    }
}

/**
 * A composable function for the standard "clickable box" (e.g., BMI, Reports).
 * This displays an IMAGE and a title in a vertical column.
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
            .animateContentSize(), // Smooth size changes if content ever changes
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp), // Padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // We use Image instead of Icon now
            Image(
                painter = painterResource(id = feature.imageId),
                contentDescription = "${feature.text} Illustration",
                modifier = Modifier
                    .size(56.dp), // Increased from 48.dp
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp)) // Increased from 6.dp
            Text(
                text = feature.text,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp, // Increased from 14.sp
                lineHeight = 17.sp, // Increased from 16.sp
                color = Color.White,
                textAlign = TextAlign.Center // Center the text
            )
        }
    }
}

/**
 * A composable function for the large "Chat With AI" card.
 * This displays text on the left and an IMAGE on the right.
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
            .animateContentSize(), // Smooth transition when size/content changes
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        // We ignore backgroundColor here and use a gradient hero background instead.
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        // Gradient hero background for Chat With AI
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
                modifier = Modifier
                    .fillMaxSize(),
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
                    modifier = Modifier
                        .size(64.dp), // Consistent size
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
        // Since DashboardScreen now needs a NavController,
        // we can use a placeholder for the preview.
        val navController = rememberNavController()
        DashboardScreen(navController = navController)
    }
}
