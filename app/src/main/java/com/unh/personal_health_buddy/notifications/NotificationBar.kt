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
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

/*
* Updated the Code using Chat GTP as there was certain errors in the original code.
* Reference Code: YouTube & Chat GPT, Gemini and Copilot
* Reference : Stack Overflow, GitHub, W3Schools
*
*/
/**
 * Simple top-of-app random nudge banner.
 * It does NOT affect NotificationScreen – it's just an in-app reminder bar.
 */
@Composable
fun RandomTopNudgeHost(
    modifier: Modifier = Modifier
) {
    // Some random “nudge” messages you want to show
    val nudges = listOf(
        "Remember to take your medications on time 💊",
        "Have you checked your BMI recently? 📊",
        "Stay hydrated – drink some water now 💧",
        "A short walk can boost your mood 🚶‍♂️",
        "Review your prescriptions to stay on track ✅",
        "Have you updated your health info today? 🩺"
    )

    var currentMessage by remember { mutableStateOf<String?>(null) }

    // Controls whether banner is visible
    var isVisible by remember { mutableStateOf(false) }

    // Loop that periodically triggers random nudges
    LaunchedEffect(Unit) {
        while (true) {
            // Wait between 20–35 seconds before showing the next nudge (shorter, feels more responsive)
            val waitMs = Random.nextLong(20_000L, 35_000L)
            delay(waitMs)

            // Pick a random message
            currentMessage = nudges.random()
            isVisible = true

            // Keep it visible for 6 seconds, unless user closes it
            val visibleFor = 6_000L
            delay(visibleFor)

            // Hide only if user hasn't already closed it
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
                .padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(22.dp))
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.98f),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.size(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Health reminder",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = currentMessage ?: "",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            isVisible = false
                        }
                )
            }
        }
    }
}
