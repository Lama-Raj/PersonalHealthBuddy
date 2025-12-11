package com.unh.personal_health_buddy.notifications

import androidx.compose.runtime.mutableStateOf
/*
* Updated the Code using Chat GTP as there was certain errors in the original code.
* Reference Code: YouTube & Chat GPT, Gemini and Copilot
* Reference : Stack Overflow, GitHub, W3Schools
*
* */
/**
 * Simple in-memory setting for the top nudge bar.
 * Default = OFF (false).

 */
object NotificationSettings {
    // Observed directly from composable with `by`
    val isTopNudgeEnabled = mutableStateOf(false)
}
