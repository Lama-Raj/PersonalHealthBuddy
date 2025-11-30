package com.unh.personal_health_buddy.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(navController: NavController) {

    // state for the current text in the input field
    var inputText by remember { mutableStateOf("") }

    // state list for all chat messages in the conversation
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    id = 1L,
                    text = "Hi, I am your health assistant.",
                    isUser = false,
                    time = getCurrentTimeLabel()
                )
            )
        )
    }

    // list state for controlling scroll position of the message list
    val listState = rememberLazyListState()

    // coroutine scope used to run scroll animations
    val coroutineScope = rememberCoroutineScope()

    // screen scaffold with blue-and-white top app bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Health Assistant",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // clear button that resets the chat messages
                    IconButton(
                        onClick = {
                            messages = listOf(
                                ChatMessage(
                                    id = 1L,
                                    text = "Hi, I am your health assistant.",
                                    isUser = false,
                                    time = getCurrentTimeLabel()
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Clear chat"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        // main content column under the top bar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // light background for chat area
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.03f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // scrollable list of chat messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // row with input field and send button
            // input area card with text field and send button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // text field where user types a message
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type your message...") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // send icon button on the right side
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val trimmed = inputText.trim()
                                val timeLabel = getCurrentTimeLabel()
                                // calculates next id based on current max id in list
                                val nextId = (messages.maxOfOrNull { it.id } ?: 0L) + 1L
                                // adds a new user message to the message list
                                val userMessage = ChatMessage(
                                    id = nextId,
                                    text = trimmed,
                                    isUser = true,
                                    time = timeLabel
                                )
                                // creates a simple bot reply based on user text
                                val botMessage = ChatMessage(
                                    id = nextId + 1L,
                                    text = getBotReply(trimmed),
                                    isUser = false,
                                    time = timeLabel
                                )
                                // updates the list with user and bot messages
                                messages = messages + userMessage + botMessage
                                // clears input text after sending
                                inputText = ""

                                // scrolls to the last message in the list
                                coroutineScope.launch {
                                    val lastIndex = messages.lastIndex
                                    if (lastIndex >= 0) {
                                        listState.animateScrollToItem(lastIndex)
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send"
                        )
                    }
                }
            }

        }
    }
}
// draws one chat bubble for a message
@Composable
fun ChatBubble(message: ChatMessage) {

    // Blue for user messages
    val userBlue = Color(0xFF0084FF)

    // bubble color depends on sender
    val bubbleColor = if (message.isUser) {
        userBlue
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    // text color depends on bubble type
    val textColor = if (message.isUser) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    // bubble corner shape depends on sender
    val bubbleShape = if (message.isUser) {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomEnd = 4.dp,
            bottomStart = 16.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomEnd = 16.dp,
            bottomStart = 4.dp
        )
    }

    // small label text for who sent the message
    val senderLabel = if (message.isUser) "You" else "Assistant"

    // row decides if bubble is aligned left or right
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
        ) {
            // small sender label above the bubble
            Text(
                text = senderLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(2.dp))

            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .background(
                        color = bubbleColor,
                        shape = bubbleShape
                    )
                    .padding(10.dp)
            ) {
                Column {
                    // main message text
                    Text(
                        text = message.text,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // small time label under the message
                    Text(
                        text = message.time,
                        color = Color.White.copy(alpha = if (message.isUser) 0.7f else 0.6f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AiChatScreenPreview() {
    PersonalHealthBuddyTheme {
        AiChatScreen(navController = rememberNavController())
    }
}

// returns a simple reply text based on user message
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

// builds a label like "10:35 PM" for the current time
private fun getCurrentTimeLabel(): String {
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(Date())
}
