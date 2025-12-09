package com.unh.personal_health_buddy.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme

/*
* Updated the Code using Chat GTP as there was certain errors in the original code.
* Reference Code: Youtube & Chat GPT, Gemini and Copilot
*
* */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    navController: NavController,
    viewModel: AiChatViewModel = viewModel()
) {
    // state list for all chat messages in the conversation
    val messages = viewModel.messages
    // state for the current text in the input field
    val inputText = viewModel.inputText
    // state to show when assistant is typing
    val isBotTyping = viewModel.isBotTyping

    // list state for controlling scroll position of the message list
    val listState = rememberLazyListState()

    // primary blue
    val primaryBlue = Color(0xFF1877F2)

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    // show suggestions mainly at start, before user has sent anything
    val showSuggestions = messages.isNotEmpty() && messages.none { it.isUser }

    // soft light-blue → white background like other screens
    val backgroundGradient = Brush.verticalGradient(
        listOf(Color(0xFFE8F1FF), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "AI Health Assistant",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = primaryBlue
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = primaryBlue
                            )
                        }
                    },
                    actions = {
                    // clear button that resets the chat messages
                        IconButton(
                            onClick = { viewModel.clearChat() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Clear chat",
                                tint = primaryBlue
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
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

            // stacked pre-written options for user to start chat
                if (showSuggestions) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                    // small label above the stack
                        Text(
                            text = "You can start with:",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                        )

                    // one rounded row for each pre-written text
                        PrewrittenStackItem(
                            text = "I feel stressed",
                            primaryBlue = primaryBlue
                        ) {
                            viewModel.onInputChange("I feel stressed. What can I do?")
                            viewModel.sendMessage()
                        }

                        PrewrittenStackItem(
                            text = "I have trouble sleeping",
                            primaryBlue = primaryBlue
                        ) {
                            viewModel.onInputChange("I have trouble sleeping. Any tips?")
                            viewModel.sendMessage()
                        }

                        PrewrittenStackItem(
                            text = "Help me understand my BMI",
                            primaryBlue = primaryBlue
                        ) {
                            viewModel.onInputChange("How can I understand my BMI?")
                            viewModel.sendMessage()
                        }
                    }
                }

            // small row that shows typing indicator when assistant is preparing a reply
                if (isBotTyping) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Assistant is typing...",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

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
                            onValueChange = { viewModel.onInputChange(it) },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Type your message...") },
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                    // send icon button on the right side, enabled only when there is text
                        IconButton(
                            onClick = { viewModel.sendMessage() },
                            enabled = inputText.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (inputText.isNotBlank()) primaryBlue else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

// one rounded row item for a pre-written text
@Composable
fun PrewrittenStackItem(
    text: String,
    primaryBlue: Color,
    onClick: () -> Unit
) {
    // soft background and rounded shape make it look friendly
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(
                color = primaryBlue.copy(alpha = 0.06f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        // text for the pre-written option
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = primaryBlue
        )
    }
}

// draws one chat bubble for a message
@Composable
fun ChatBubble(message: ChatMessage) {

    val userBlue = Color(0xFF1877F2)
    val assistantBubble = Color(0xFFF3F6FF)
    val assistantText = Color(0xFF102A43)

    val bubbleColor = if (message.isUser) {
        userBlue
    } else {
        assistantBubble
    }

    val textColor = if (message.isUser) {
        Color.White
    } else {
        assistantText
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
                        color = if (message.isUser)
                            Color.White.copy(alpha = 0.7f)
                        else
                            assistantText.copy(alpha = 0.6f),
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
