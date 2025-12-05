package com.unh.personal_health_buddy

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

// FAQ model
data class FAQItem(val question: String, val answer: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(navController: NavHostController? = null) {

    val faqList = listOf(
        FAQItem("What is the Personal Health Buddy app?", "It is a centralized platform to manage your personal health data."),
        FAQItem("How do I create my health profile?", "Enter personal details, allergies, medications, email and emergency contacts in Account."),
        FAQItem("How is my data kept secure?", "We use Firebase Authentication to protect your information."),
        FAQItem("Can I see nearby health services?", "Yes — with location enabled, nearby hospitals/clinics appear on the map."),
        FAQItem("Will I receive reminders?", "Push notifications remind you about meds, and health checks."),
        FAQItem("Can I upload health documents or photos?", "Yes — upload and store medical documents securely."),
        FAQItem("Can I delete my account?", "Yes — delete your account and its data within Account Settings."),
        FAQItem("Does the app support biometric login?", "Yes — fingerprint and Face Unlock are supported where available.")
    )

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "FAQ",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController?.let {
                            if (it.previousBackStackEntry != null) {
                                it.popBackStack()
                            } else {
                                it.navigate("profile") { launchSingleTop = true }
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Back"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(faqList) { item ->
                InteractiveFAQItem(item)
            }
        }
    }
}

@Composable
fun InteractiveFAQItem(item: FAQItem) {
    var expanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = if (expanded) listOf(Color(0xFFBBDEFB), Color(0xFFE3F2FD))
                        else listOf(Color(0xFFE3F2FD), Color(0xFFE3F2FD))
                    )
                )
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.question,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF0D47A1)
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    modifier = Modifier.rotate(rotation),
                    tint = Color(0xFF0D47A1)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFFFF))
                        .padding(12.dp),
                    color = Color(0xFF0D47A1)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FAQScreenPreview() {
    val navController = rememberNavController()
    FAQScreen(navController)
}
