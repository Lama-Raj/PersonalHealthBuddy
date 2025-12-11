package com.unh.personal_health_buddy.profile

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.ui.theme.TextColor
import com.unh.personal_health_buddy.ui.theme.White

/*
* Updated the Code using Chat GTP as there was certain errors in the original code.
* Reference Code: YouTube & Chat GPT, Gemini and Copilot
* Reference : Stack Overflow, GitHub, W3Schools
*
* */
val FAQ_PrimaryBlue = Color(0xFF1877F2)
val FAQ_LightBlueBg = Color(0xFFF3F6FF)

// FAQ model
data class FAQItem(val question: String, val answer: String)

@Composable
fun FAQScreen(navController: NavHostController? = null) {

    val faqList = remember {
        listOf(
            FAQItem(
                "What is the Personal Health Buddy app?",
                "It is a centralized platform to manage your personal health data, emergency contacts, and medical info in one secure place."
            ),
            FAQItem(
                "How do I create my health profile?",
                "Go to the 'Profile' tab and click 'Edit Details' in the menu. Fill in your personal details, allergies, and emergency contacts."
            ),
            FAQItem(
                "How is my data kept secure?",
                "We use Google Firebase Authentication and secure cloud storage. Your data is encrypted and only accessible when you are signed in."
            ),
            FAQItem(
                "Can I see nearby health services?",
                "Yes. Navigate to the Map screen to view nearby hospitals and clinics using Google Maps integration."
            ),
            FAQItem(
                "Will I receive reminders?",
                "Yes. You can enable 'Top Nudges' in your Profile settings to receive helpful health reminders at the top of the app."
            ),
            FAQItem(
                "Can I upload a profile photo?",
                "Absolutely. Go to Account Details, tap 'Edit', and click on the profile picture placeholder to take a photo or choose from your gallery."
            ),
            FAQItem(
                "How do I delete my account?",
                "In the Account screen menu, select 'Delete Account'. This will permanently remove all your data from our servers."
            ),
            FAQItem(
                "Does the app support biometric login?",
                "Yes. If your device supports it, you can use fingerprint or face unlock via the secure login screen."
            )
        )
    }

    // Gradient Background (Same as AccountScreen)
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(FAQ_LightBlueBg, White)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        // --- 1. Custom Top Header ---
        FAQTopHeader(navController)

        // --- 2. Content Area (Rounded Sheet) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(FAQ_LightBlueBg)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Common Questions",
                        style = MaterialTheme.typography.labelLarge,
                        color = FAQ_PrimaryBlue,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                }

                items(faqList) { item ->
                    FAQRow(item = item)
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun FAQTopHeader(navController: NavHostController?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController?.let {
                        if (it.previousBackStackEntry != null) it.popBackStack()
                        else it.navigate("profile") { launchSingleTop = true }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Help & Support",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = FAQ_PrimaryBlue
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Optional: Big Icon or Illustration
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, FAQ_PrimaryBlue.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = null,
                tint = FAQ_PrimaryBlue,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "How can we help you?",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = TextColor
        )
    }
}

@Composable
fun FAQRow(item: FAQItem) {
    var expanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "arrow"
    )

    // Card-like container matching Account Form styling
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .clickable { expanded = !expanded }
            .padding(16.dp)
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.question,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = TextColor,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = "Expand",
                tint = FAQ_PrimaryBlue,
                modifier = Modifier
                    .rotate(rotation)
                    .size(24.dp)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = FAQ_PrimaryBlue.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.answer,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp
                ),
                color = TextColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FAQScreenPreview() {
    val navController = rememberNavController()
    FAQScreen(navController)
}