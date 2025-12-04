package com.unh.personal_health_buddy.chat


/*This class is one chat message.
holds one chat message for the chat screen
id = unique number
text = message text
isUser = true if user send it, false if bot send it
 */
data class ChatMessage(
    val id: Long,
    val text: String,
    val isUser: Boolean,
    val time: String // Added time property / time stamp
)
