package com.unh.personal_health_buddy.notifications

import androidx.compose.runtime.mutableStateOf

/**
 * Simple in-memory setting for the top nudge bar.
 * Default = OFF (false).

 */
object NotificationSettings {
    // Observed directly from composable with `by`
    val isTopNudgeEnabled = mutableStateOf(false)
}
