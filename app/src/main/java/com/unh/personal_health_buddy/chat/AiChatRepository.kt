package com.unh.personal_health_buddy.chat

// defines how we get a reply text for a user message
interface ChatRepository {
    suspend fun getBotReply(userText: String): String
}

// simple default implementation that uses keyword rules
class DefaultChatRepository : ChatRepository {

    override suspend fun getBotReply(userText: String): String {
        val lower = userText.lowercase()
/* Text message user eill get based on their message which is static for now*/
        return when {
            "hello" in lower || "hi" in lower ->
                "Hello, how can I help you today?"

            "bmi" in lower ->
                "You can use the BMI screen to check your body mass index."

            "sleep" in lower ->
                "Try to keep a regular sleep time, reduce screens before bed, and avoid heavy meals late."

            "stress" in lower || "anxious" in lower ->
                "Try slow breathing, a short walk, and talking to someone you trust. If this feels serious, contact a professional."

            "diet" in lower || "food" in lower ->
                "Aim for more vegetables, fruits, whole grains, and enough water. Limit sugary drinks and very salty snacks."

            "exercise" in lower || "workout" in lower ->
                "Even 20–30 minutes of walking most days can help. Choose something you enjoy so you can stay consistent."

            "thank" in lower ->
                "You are welcome."

            else ->
                "I read: \"$userText\". I can give simple general tips, but this app does not replace a doctor."
        }
    }
}
