package com.unh.personal_health_buddy.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random
/*
* Updated the Code using Chat GTP as there was certain errors in the original code.
* Reference Code: YouTube & Chat GPT, Gemini and Copilot
* Reference : Stack Overflow, GitHub, W3Schools
*
* */
/**
 * Simple top-of-app random nudge banner.
 * Also logs each nudge into InAppNotificationManager so it appears
 * later in NotificationScreen as history.
 */
@Composable
fun RandomTopNudgeHost(
    modifier: Modifier = Modifier
) {
    // Random “nudge” messages
    val nudges = listOf(
        "Remember to take your medications on time 💊",
        "Have you checked your BMI recently? 📊",
        "Stay hydrated – drink some water now 💧",
        "A short walk can boost your mood 🚶‍♂️",
        "Review your prescriptions to stay on track ✅",
        "Have you updated your health info today? 🩺"
    )

    var currentMessage by remember { mutableStateOf<String?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    // Loop that periodically triggers random nudges
    LaunchedEffect(Unit) {
        while (true) {
            // Wait between 20–40 seconds before showing the next nudge
            val waitMs = Random.nextLong(20_000L, 40_000L)
            delay(waitMs)

            val msg = nudges.random()
            currentMessage = msg
            isVisible = true

            // 🔹 Add this nudge into in-app notifications so it shows in NotificationScreen
            //    Title is kept short; message is the full text.
            InAppNotificationManager.addNotification(
                title = "Health Reminder",
                message = msg
            )

            // Keep banner visible for ~5 seconds unless user closes it
            delay(5_000L)
            isVisible = false
        }
    }

    AnimatedVisibility(
        visible = isVisible && currentMessage != null,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(18.dp))
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.97f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = currentMessage ?: "",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.size(10.dp))

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                    modifier = Modifier
                        .size(22.dp)
                        .clickable {
                            isVisible = false
                        }
                )
            }
        }
    }
}
