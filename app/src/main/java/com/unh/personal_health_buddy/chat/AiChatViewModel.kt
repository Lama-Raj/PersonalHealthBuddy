package com.unh.personal_health_buddy.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AiChatViewModel : ViewModel() {

    // text typed by the user in the input field
    var inputText by mutableStateOf("")
        private set

    // list of all messages shown in the chat
    var messages by mutableStateOf(
        listOf(
            ChatMessage(
                id = 1L,
                text = "Hi, I am your health assistant.",
                isUser = false,
                time = getCurrentTimeLabel()
            )
        )
    )
        private set

    // updates the text in the input field
    fun onInputChange(newText: String) {
        inputText = newText
    }

    // clears all messages and shows the greeting again
    fun clearChat() {
        messages = listOf(
            ChatMessage(
                id = 1L,
                text = "Hi, I am your health assistant.",
                isUser = false,
                time = getCurrentTimeLabel()
            )
        )
        inputText = ""
    }

    // creates a user message and bot reply and adds them to the list
    fun sendMessage() {
        val trimmed = inputText.trim()
        if (trimmed.isEmpty()) return

        val timeLabel = getCurrentTimeLabel()
        val nextId = (messages.maxOfOrNull { it.id } ?: 0L) + 1L

        val userMessage = ChatMessage(
            id = nextId,
            text = trimmed,
            isUser = true,
            time = timeLabel
        )

        val botMessage = ChatMessage(
            id = nextId + 1L,
            text = getBotReply(trimmed),
            isUser = false,
            time = timeLabel
        )

        messages = messages + userMessage + botMessage
        inputText = ""
    }

    // returns a reply text based on the user message
    private fun getBotReply(userText: String): String {
        val lower = userText.lowercase()

        return when {
            "hello" in lower || "hi" in lower ->
                "Hello, how can I help you today?"

            "bmi" in lower ->
                "You can use the BMI screen to check your body mass index."

            "stress" in lower || "anxious" in lower ->
                "Try slow breathing and a short walk. If you feel very bad, talk to a professional."

            "thank" in lower ->
                "You are welcome."

            else ->
                "I read: \"$userText\". I am a simple helper in this app."
        }
    }

    // builds a time label like "10:35 PM"
    private fun getCurrentTimeLabel(): String {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return formatter.format(Date())
    }
}
